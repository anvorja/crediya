// domain/usecase/src/main/java/com/crediya/solicitudes/usecase/crearsolicitud/CrearSolicitudUseCase.java
package com.crediya.solicitudes.usecase.crearsolicitud;

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
 * Implementa validaciones de negocio y flujo reactivo completo
 * Usa el modelo Solicitud existente SIN Lombok
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
        return validarDatosBasicos(solicitud)
                .then(validarSolicitudUnica(solicitud))
                .then(Mono.just(solicitud))
                .flatMap(this::enriquecerConValidacionesExternas)
                .flatMap(this::evaluarYActualizarEstado)
                .flatMap(solicitudRepository::guardar)
                .flatMap(solicitudGuardada ->
                        notificarYPublicarEventos(solicitudGuardada)
                                .onErrorResume(error -> {
                                    // Log error pero no fallar el flujo principal
                                    System.err.println("Error en notificaciones: " + error.getMessage());
                                    return Mono.empty();
                                })
                                .thenReturn(solicitudGuardada)
                )
                .onErrorMap(IllegalArgumentException.class, ex ->
                        new DatosSolicitudInvalidosException("Datos inválidos: " + ex.getMessage()))
                .onErrorMap(Exception.class, ex -> {
                    if (ex instanceof DatosSolicitudInvalidosException ||
                            ex instanceof SolicitudDuplicadaException) {
                        return ex;
                    }
                    return new DatosSolicitudInvalidosException("Error procesando solicitud: " + ex.getMessage());
                });
    }

    /**
     * Valida datos básicos de la solicitud - USA TU MODELO EXISTENTE
     */
    private Mono<Void> validarDatosBasicos(Solicitud solicitud) {
        return Mono.fromRunnable(() -> {
            if (solicitud == null) {
                throw new IllegalArgumentException("Solicitud no puede ser nula");
            }

            try {
                // Usar tu método de validación existente que ya incluye CA-3
                solicitud.validarDatos();
            } catch (DatosSolicitudInvalidosException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        });
    }

    /**
     * Valida que no exista una solicitud activa para el mismo documento
     */
    private Mono<Void> validarSolicitudUnica(Solicitud solicitud) {
        return solicitudRepository.existeSolicitudActivaPorDocumento(solicitud.getNumeroDocumento())
                .flatMap(existe -> {
                    if (Boolean.TRUE.equals(existe)) {
                        return Mono.error(new SolicitudDuplicadaException(
                                "Ya existe una solicitud activa para el documento: " + solicitud.getNumeroDocumento()));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Solicitud> enriquecerConValidacionesExternas(Solicitud solicitud) {
        return Mono.just(solicitud)
                .flatMap(this::validarDocumento)
                .flatMap(this::consultarHistorialCrediticio)
                .flatMap(this::validarInformacionFinanciera)
                .onErrorResume(error -> {
                    // En caso de error en validaciones externas, continuar con observaciones
                    String obsActual = solicitud.getObservaciones() != null ? solicitud.getObservaciones() : "";
                    solicitud.setObservaciones(obsActual + "Error en validaciones externas: " + error.getMessage() + "; ");
                    return Mono.just(solicitud);
                });
    }

    private Mono<Solicitud> validarDocumento(Solicitud solicitud) {
        return validacionExternaGateway.validarDocumento(solicitud.getNumeroDocumento())
                .map(validacion -> {
                    if (!validacion.esValido()) {
                        String obsActual = solicitud.getObservaciones() != null ? solicitud.getObservaciones() : "";
                        solicitud.setObservaciones(obsActual + "Documento no válido según fuentes externas; ");
                    }
                    return solicitud;
                })
                .onErrorReturn(solicitud);
    }

    private Mono<Solicitud> consultarHistorialCrediticio(Solicitud solicitud) {
        return validacionExternaGateway.consultarHistorialCrediticio(solicitud.getNumeroDocumento())
                .map(historial -> {
                    if (historial.tieneReportesNegativos()) { // CORRECTO: usar tieneReportesNegativos()
                        // Usar tu método setObservaciones existente
                        String obsActual = solicitud.getObservaciones() != null ? solicitud.getObservaciones() : "";
                        solicitud.setObservaciones(obsActual + "Historial crediticio negativo detectado; ");
                    }
                    if (historial.puntajeCrediticio() < 500) { // CORRECTO: usar puntajeCrediticio()
                        String obsActual = solicitud.getObservaciones() != null ? solicitud.getObservaciones() : "";
                        solicitud.setObservaciones(obsActual + "Score crediticio bajo: " + historial.puntajeCrediticio() + "; ");
                    }
                    return solicitud;
                })
                .onErrorReturn(solicitud);
    }

    private Mono<Solicitud> validarInformacionFinanciera(Solicitud solicitud) {
        return validacionExternaGateway.validarInformacionFinanciera(
                        solicitud.getNumeroDocumento(),
                        solicitud.getIngresosMensuales())
                .map(validacion -> {
                    if (!validacion.ingresosVerificados()) {
                        String obsActual = solicitud.getObservaciones() != null ? solicitud.getObservaciones() : "";
                        solicitud.setObservaciones(obsActual + "Validación financiera falló: " + validacion.observaciones() + "; ");
                    }
                    return solicitud;
                })
                .onErrorReturn(solicitud);
    }

    /**
     * Evalúa la solicitud y actualiza su estado
     */
    private Mono<Solicitud> evaluarYActualizarEstado(Solicitud solicitud) {
        return Mono.fromCallable(() -> {
            // CA-2: Usar tu método existente
            solicitud.asignarEstadoInicial();
            return solicitud;
        });
    }

    /**
     * Notifica y publica eventos de la solicitud creada
     */
    private Mono<Void> notificarYPublicarEventos(Solicitud solicitud) {
        Mono<Void> notificacion = notificationGateway.notificarSolicitudCreada(solicitud)
                .onErrorResume(error -> {
                    System.err.println("Error enviando notificación: " + error.getMessage());
                    return Mono.empty();
                });

        Mono<Void> evento = eventPublisherGateway.publicarEventoSolicitudCreada(solicitud)
                .onErrorResume(error -> {
                    System.err.println("Error publicando evento: " + error.getMessage());
                    return Mono.empty();
                });

        return Mono.when(notificacion, evento);
    }
}