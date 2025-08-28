// domain/usecase/src/main/java/com/crediya/solicitudes/usecase/crearsolicitud/CrearSolicitudUseCase.java
package com.crediya.solicitudes.usecase.crearsolicitud;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.exception.SolicitudDuplicadaException;
import com.crediya.solicitudes.model.solicitud.exception.DatosSolicitudInvalidosException;
import com.crediya.solicitudes.model.solicitud.gateways.EventPublisherGateway;
import com.crediya.solicitudes.model.solicitud.gateways.NotificationGateway;
import com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import com.crediya.solicitudes.model.solicitud.gateways.ValidacionExternaGateway;
import reactor.core.publisher.Mono;

/**
 * Use Case para crear solicitudes de préstamo
 */
public class CrearSolicitudUseCase {
    private final SolicitudRepository solicitudRepository;
    private final NotificationGateway notificationGateway;
    private final EventPublisherGateway eventPublisherGateway;
    private final ValidacionExternaGateway validacionExternaGateway;

    public CrearSolicitudUseCase(SolicitudRepository solicitudRepository,
                                 NotificationGateway notificationGateway,
                                 EventPublisherGateway eventPublisherGateway,
                                 ValidacionExternaGateway validacionExternaGateway) {
        this.solicitudRepository = solicitudRepository;
        this.notificationGateway = notificationGateway;
        this.eventPublisherGateway = eventPublisherGateway;
        this.validacionExternaGateway = validacionExternaGateway;
    }

    /**
     * Ejecuta la creación de solicitud - COMPLETAMENTE REACTIVO
     * @param solicitud la solicitud a crear
     * @return Mono<Solicitud> con la solicitud creada
     */
    public Mono<Solicitud> ejecutar(Solicitud solicitud) {
        return validarSolicitudUnica(solicitud)
                .then(validarDatosBasicos(solicitud))
                .flatMap(this::enriquecerConValidacionesExternas)
                .flatMap(this::evaluarYActualizarEstado)
                .flatMap(solicitudRepository::guardar)
                .flatMap(solicitudGuardada ->
                        notificarYPublicarEventos(solicitudGuardada)
                                .thenReturn(solicitudGuardada)
                )
                // Manejo de errores reactivo
                .onErrorMap(IllegalArgumentException.class, ex ->
                        new DatosSolicitudInvalidosException("Datos inválidos: " + ex.getMessage()))
                .onErrorMap(Exception.class, ex -> {
                    // Log del error aquí si fuera necesario
                    return ex instanceof DatosSolicitudInvalidosException ||
                            ex instanceof SolicitudDuplicadaException ? ex :
                            new RuntimeException("Error interno procesando solicitud", ex);
                });
    }

    /**
     * Valida que no exista una solicitud activa para el documento
     */
    private Mono<Void> validarSolicitudUnica(Solicitud solicitud) {
        return solicitudRepository.existeSolicitudActivaPorDocumento(solicitud.getNumeroDocumento())
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.<Void>error(new SolicitudDuplicadaException(
                                "Ya existe una solicitud activa para el documento: " +
                                        solicitud.getNumeroDocumento()));
                    }
                    return Mono.empty();
                });
    }

    /**
     * Valida datos básicos de la solicitud de forma reactiva
     */
    private Mono<Solicitud> validarDatosBasicos(Solicitud solicitud) {
        return Mono.fromCallable(() -> {
            // Ejecutar validación síncrona del dominio dentro del contexto reactivo
            solicitud.validarDatos();
            return solicitud;
        });
    }

    /**
     * Enriquece la solicitud con validaciones externas de forma reactiva
     */
    private Mono<Solicitud> enriquecerConValidacionesExternas(Solicitud solicitud) {
        // Validar documento de forma reactiva
        Mono<Void> validarDocumento = validacionExternaGateway.validarDocumento(solicitud.getNumeroDocumento())
                .doOnNext(validacion -> {
                    if (!validacion.esValido() ||
                            !solicitud.getNombres().equalsIgnoreCase(validacion.nombre()) ||
                            !solicitud.getApellidos().equalsIgnoreCase(validacion.apellido())) {

                        agregarObservacion(solicitud, "Datos del documento requieren verificación manual");
                    }
                })
                .then()
                .onErrorResume(ex -> Mono.empty()); // Continuar si falla la validación de documento

        // Consultar historial crediticio de forma reactiva
        Mono<Void> consultarHistorial = validacionExternaGateway.consultarHistorialCrediticio(solicitud.getNumeroDocumento())
                .doOnNext(historial -> {
                    if (historial.tieneReportesNegativos() || historial.puntajeCrediticio() < 500) {
                        agregarObservacion(solicitud,
                                "Historial crediticio requiere revisión (Score: " +
                                        historial.puntajeCrediticio() + ")");
                    }
                })
                .then()
                .onErrorResume(ex -> Mono.empty()); // Continuar si falla la consulta de historial

        // Ejecutar ambas validaciones en paralelo y retornar la solicitud
        return Mono.when(validarDocumento, consultarHistorial)
                .thenReturn(solicitud);
    }

    /**
     * Evalúa capacidad de pago y actualiza estado
     */
    private Mono<Solicitud> evaluarYActualizarEstado(Solicitud solicitud) {
        return Mono.fromCallable(() -> {
            // Evaluar capacidad de pago automáticamente
            boolean tieneCapacidadPago = solicitud.evaluarCapacidadPago();

            if (!tieneCapacidadPago) {
                solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
                agregarObservacion(solicitud, "Requiere evaluación manual por capacidad de pago");
            } else {
                // HU2: "Se registra automáticamente con estado inicial Pendiente de revisión"
                solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
            }

            return solicitud;
        });
    }

    /**
     * Notifica y publica eventos de forma reactiva (non-blocking)
     */
    private Mono<Void> notificarYPublicarEventos(Solicitud solicitudGuardada) {
        // Ejecutar notificaciones en paralelo sin bloquear
        Mono<Void> notificar = notificationGateway.notificarSolicitudCreada(solicitudGuardada)
                .onErrorResume(ex -> {
                    // Log warning pero no fallar el flujo principal
                    // logger.warn("Error enviando notificación: {}", ex.getMessage());
                    return Mono.empty();
                });

        Mono<Void> publicarEvento = eventPublisherGateway.publicarEventoSolicitudCreada(solicitudGuardada)
                .onErrorResume(ex -> {
                    // Log warning pero no fallar el flujo principal
                    // logger.warn("Error publicando evento: {}", ex.getMessage());
                    return Mono.empty();
                });

        // Ejecutar ambas operaciones en paralelo
        return Mono.when(notificar, publicarEvento);
    }

    /**
     * Método auxiliar para agregar observaciones (mantiene lógica actual)
     */
    private void agregarObservacion(Solicitud solicitud, String nuevaObservacion) {
        String observaciones = solicitud.getObservaciones();
        if (observaciones == null || observaciones.trim().isEmpty()) {
            solicitud.setObservaciones(nuevaObservacion);
        } else {
            solicitud.setObservaciones(observaciones + ". " + nuevaObservacion);
        }
    }
}