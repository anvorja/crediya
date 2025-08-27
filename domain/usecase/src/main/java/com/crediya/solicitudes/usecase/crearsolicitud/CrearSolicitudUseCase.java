// java/com/crediya/solicitudes/usecase/crearsolicitud/CrearSolicitudUseCase.java
package com.crediya.solicitudes.usecase.crearsolicitud;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.exception.SolicitudDuplicadaException;
import com.crediya.solicitudes.model.solicitud.gateways.*;
import com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import com.crediya.solicitudes.model.solicitud.gateways.NotificationGateway;
import com.crediya.solicitudes.model.solicitud.gateways.EventPublisherGateway;
import com.crediya.solicitudes.model.solicitud.gateways.ValidacionExternaGateway;

/**
 * Caso de uso PURO para crear una nueva solicitud de préstamo
 * SIN DEPENDENCIAS EXTERNAS: Sin Reactor, sin logging, sin frameworks
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
        // Si tiene observaciones de validaciones externas, requiere revisión manual
        if (solicitud.getObservaciones() != null &&
                !solicitud.getObservaciones().trim().isEmpty()) {
            return; // Mantiene estado PENDIENTE_REVISION
        }

        // Evaluar automáticamente usando lógica de dominio pura
        solicitud.preAprobarAutomaticamente();
    }

    private void notificarYPublicarEventos(Solicitud solicitud) {
        try {
            // Notificar al solicitante (mejor esfuerzo)
            notificationGateway.notificarEstadoSolicitud(
                    solicitud, NotificationGateway.TipoNotificacion.EMAIL);

            // Notificar a administradores si requiere revisión
            if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE_REVISION) {
                notificationGateway.notificarSolicitudRequiereRevision(solicitud);
            }

            // Publicar evento de solicitud creada
            eventPublisherGateway.publicarSolicitudCreada(solicitud);

        } catch (Exception e) {
            // Los errores de notificación no deben fallar el caso de uso principal
            // En infraestructura se manejará el logging de estos errores
        }
    }
}