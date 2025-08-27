// domain/usecase/src/main/java/com/crediya/solicitudes/usecase/crearsolicitud/CrearSolicitudUseCase.java
package com.crediya.solicitudes.usecase.crearsolicitud;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.exception.SolicitudDuplicadaException;
import com.crediya.solicitudes.model.solicitud.gateways.EventPublisherGateway;
import com.crediya.solicitudes.model.solicitud.gateways.NotificationGateway;
import com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import com.crediya.solicitudes.model.solicitud.gateways.ValidacionExternaGateway;

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
     * Ejecuta la creación de solicitud - COMPLETAMENTE SÍNCRONO
     */
    public Solicitud ejecutar(Solicitud solicitud) {
        // 1. Validar unicidad
        validarSolicitudUnica(solicitud);

        // 2. Validar datos básicos
        solicitud.validarDatos();

        // 3. Enriquecer con validaciones externas
        enriquecerConValidacionesExternas(solicitud);

        // 4. Evaluar y actualizar estado
        evaluarYActualizarEstado(solicitud);

        // 5. Guardar solicitud
        Solicitud solicitudGuardada = solicitudRepository.guardar(solicitud);

        // 6. Notificar y publicar eventos (mejor esfuerzo)
        notificarYPublicarEventos(solicitudGuardada);

        return solicitudGuardada;
    }

    private void validarSolicitudUnica(Solicitud solicitud) {
        boolean existe = solicitudRepository.existeSolicitudActivaPorDocumento(
                solicitud.getNumeroDocumento());

        if (existe) {
            throw new SolicitudDuplicadaException(
                    "Ya existe una solicitud activa para el documento: " +
                            solicitud.getNumeroDocumento());
        }
    }

    private void enriquecerConValidacionesExternas(Solicitud solicitud) {
        // Validar documento (opcional)
        validacionExternaGateway.validarDocumento(solicitud.getNumeroDocumento())
                .ifPresent(validacion -> {
                    if (!validacion.esValido() ||
                            !solicitud.getNombres().equalsIgnoreCase(validacion.nombre()) ||
                            !solicitud.getApellidos().equalsIgnoreCase(validacion.apellido())) {

                        agregarObservacion(solicitud, "Datos del documento requieren verificación manual");
                    }
                });

        // Consultar historial crediticio (opcional)
        validacionExternaGateway.consultarHistorialCrediticio(solicitud.getNumeroDocumento())
                .ifPresent(historial -> {
                    if (historial.tieneReportesNegativos() || historial.puntajeCrediticio() < 500) {
                        agregarObservacion(solicitud,
                                "Historial crediticio requiere revisión (Score: " +
                                        historial.puntajeCrediticio() + ")");
                    }
                });
    }

    private void agregarObservacion(Solicitud solicitud, String nuevaObservacion) {
        String observaciones = solicitud.getObservaciones();
        if (observaciones == null || observaciones.trim().isEmpty()) {
            solicitud.setObservaciones(nuevaObservacion);
        } else {
            solicitud.setObservaciones(observaciones + ". " + nuevaObservacion);
        }
    }

    private void evaluarYActualizarEstado(Solicitud solicitud) {
        // Evaluar capacidad de pago automáticamente
        boolean tieneCapacidadPago = solicitud.evaluarCapacidadPago();

        if (!tieneCapacidadPago) {
            // Si no tiene capacidad de pago, mantener en PENDIENTE_REVISION
            solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
            agregarObservacion(solicitud, "Requiere evaluación manual por capacidad de pago");
        } else {
            // Si tiene capacidad, puede mantenerse en PENDIENTE_REVISION para revisión humana
            solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        }
    }

    private void notificarYPublicarEventos(Solicitud solicitudGuardada) {
        try {
            notificationGateway.notificarSolicitudCreada(solicitudGuardada);
        } catch (Exception e) {
            // Log del error pero no fallar el proceso principal
            // logger.warn("Error enviando notificación: {}", e.getMessage());
        }

        try {
            eventPublisherGateway.publicarEventoSolicitudCreada(solicitudGuardada);
        } catch (Exception e) {
            // Log del error pero no fallar el proceso principal
            // logger.warn("Error publicando evento: {}", e.getMessage());
        }
    }
}