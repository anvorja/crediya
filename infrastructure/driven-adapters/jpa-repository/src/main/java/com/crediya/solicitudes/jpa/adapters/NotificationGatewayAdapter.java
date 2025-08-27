package com.crediya.solicitudes.jpa.adapters;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.gateways.NotificationGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementación mock del gateway de notificaciones
 * En un proyecto real, aquí se integraría con servicios como:
 * - AWS SES/SNS
 * - SendGrid
 * - Sistemas internos de notificación
 */
@Slf4j
@Component
public class NotificationGatewayAdapter implements NotificationGateway {

    @Override
    public boolean notificarEstadoSolicitud(Solicitud solicitud, TipoNotificacion tipo) {
        try {
            // SIMULACIÓN - En un proyecto real aquí iría la integración real
            log.info("📧 NOTIFICACIÓN ENVIADA:");
            log.info("   • Tipo: {}", tipo);
            log.info("   • Destinatario: {} ({})",
                    solicitud.getNombreCompleto(), solicitud.getEmail());
            log.info("   • Solicitud: {} - Estado: {}",
                    solicitud.getId(), solicitud.getEstado());
            log.info("   • Mensaje: Su solicitud de crédito {} ha cambiado al estado: {}",
                    solicitud.getTipoCredito(), solicitud.getEstado().getDescripcion());

            // Simular lógica según tipo
            return switch (tipo) {
                case EMAIL -> enviarEmail(solicitud);
                case SMS -> enviarSms(solicitud);
                case PUSH -> enviarPushNotification(solicitud);
                case TODOS -> enviarEmail(solicitud) &&
                        enviarSms(solicitud) &&
                        enviarPushNotification(solicitud);
                default -> {
                    log.warn("Tipo de notificación no soportado: {}", tipo);
                    yield false;
                }
            };

        } catch (Exception e) {
            log.error("Error enviando notificación para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean notificarNuevaSolicitudAAdministradores(Solicitud solicitud) {
        try {
            log.info("👨‍💼 NOTIFICACIÓN A ADMINISTRADORES:");
            log.info("   • Nueva solicitud recibida: {}", solicitud.getId());
            log.info("   • Cliente: {} ({})",
                    solicitud.getNombreCompleto(), solicitud.getNumeroDocumento());
            log.info("   • Monto: ${:,.2f}", solicitud.getMontoSolicitado());
            log.info("   • Tipo: {}", solicitud.getTipoCredito());
            log.info("   • Estado inicial: {}", solicitud.getEstado().getDescripcion());

            // En un proyecto real:
            // - Enviar email a lista de administradores
            // - Crear notificación en dashboard administrativo
            // - Posible webhook a sistemas externos

            return true;

        } catch (Exception e) {
            log.error("Error notificando nueva solicitud a administradores: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean notificarSolicitudRequiereRevision(Solicitud solicitud) {
        try {
            log.info("⚠️  NOTIFICACIÓN DE REVISIÓN REQUERIDA:");
            log.info("   • Solicitud: {} requiere revisión manual", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Razón: {}", solicitud.getObservaciones());
            log.info("   • Monto solicitado: ${:,.2f}", solicitud.getMontoSolicitado());

            // En un proyecto real:
            // - Asignar a cola de revisión manual
            // - Notificar a analistas de crédito
            // - Actualizar dashboard de revisiones pendientes

            return true;

        } catch (Exception e) {
            log.error("Error notificando solicitud que requiere revisión: {}", e.getMessage());
            return false;
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES PARA SIMULACIÓN
    // ============================================================

    private boolean enviarEmail(Solicitud solicitud) {
        // En un proyecto real: integración con AWS SES, SendGrid, etc.
        log.debug("   ✉️  Email enviado a: {}", solicitud.getEmail());
        return true;
    }

    private boolean enviarSms(Solicitud solicitud) {
        // En un proyecto real: integración con AWS SNS, Twilio, etc.
        log.debug("   📱 SMS enviado a: {}", solicitud.getTelefono());
        return true;
    }

    private boolean enviarPushNotification(Solicitud solicitud) {
        // En un proyecto real: integración con Firebase, AWS SNS Mobile Push, etc.
        log.debug("   🔔 Push notification enviada");
        return true;
    }
}