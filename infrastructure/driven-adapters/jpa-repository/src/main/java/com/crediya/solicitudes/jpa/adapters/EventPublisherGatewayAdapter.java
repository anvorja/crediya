// infrastructure/driven-adapters/jpa-repository/src/main/java/com/crediya/solicitudes/jpa/adapters/EventPublisherGatewayAdapter.java
package com.crediya.solicitudes.jpa.adapters;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.gateways.EventPublisherGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementación mock del gateway de publicación de eventos
 * En un proyecto real, aquí se integraría con:
 * - AWS SQS/SNS
 * - Apache Kafka
 * - RabbitMQ
 * - Azure Service Bus
 * - Google Cloud Pub/Sub
 */
@Slf4j
@Component
public class EventPublisherGatewayAdapter implements EventPublisherGateway {

    @Override
    public void publicarEventoSolicitudCreada(Solicitud solicitud) {
        try {
            log.info("🚀 EVENTO PUBLICADO: SolicitudCreada");
            log.info("   • ID Solicitud: {}", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Documento: {}", solicitud.getNumeroDocumento());
            log.info("   • Email: {}", solicitud.getEmail());
            log.info("   • Estado inicial: {}", solicitud.getEstado().getDescripcion());
            log.info("   • Monto: ${:,.2f}", solicitud.getMontoSolicitado());
            log.info("   • Tipo crédito: {}", solicitud.getTipoCredito());
            log.info("   • Fecha: {}", solicitud.getFechaCreacion());

            // En un proyecto real aquí se construiría y publicaría el evento:
            // SolicitudCreadaEvent event = SolicitudCreadaEvent.builder()
            //     .solicitudId(solicitud.getId())
            //     .numeroDocumento(solicitud.getNumeroDocumento())
            //     .nombreCompleto(solicitud.getNombreCompleto())
            //     .email(solicitud.getEmail())
            //     .estado(solicitud.getEstado().name())
            //     .monto(solicitud.getMontoSolicitado())
            //     .tipoCredito(solicitud.getTipoCredito())
            //     .fechaCreacion(solicitud.getFechaCreacion())
            //     .timestamp(Instant.now())
            //     .eventId(UUID.randomUUID().toString())
            //     .version("1.0")
            //     .build();
            //
            // eventPublisher.publish("solicitudes.created", event);

            log.debug("✅ Evento SolicitudCreada publicado exitosamente para solicitud {}", solicitud.getId());

        } catch (Exception e) {
            log.error("❌ Error publicando evento SolicitudCreada para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
            // En producción, podríamos:
            // - Reintentar la publicación con exponential backoff
            // - Guardar en dead letter queue
            // - Alertar al equipo sobre fallos de eventos
            // - Métricas de eventos fallidos
            throw new RuntimeException("Error publicando evento de solicitud creada", e);
        }
    }

    @Override
    public void publicarEventoCambioEstado(Solicitud solicitud) {
        try {
            log.info("🔄 EVENTO PUBLICADO: CambioEstadoSolicitud");
            log.info("   • ID Solicitud: {}", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Estado actual: {}", solicitud.getEstado().getDescripcion());
            log.info("   • Fecha actualización: {}", solicitud.getFechaActualizacion());
            log.info("   • Observaciones: {}", solicitud.getObservaciones());

            // En un proyecto real:
            // CambioEstadoEvent event = CambioEstadoEvent.builder()
            //     .solicitudId(solicitud.getId())
            //     .estadoNuevo(solicitud.getEstado().name())
            //     .fechaCambio(solicitud.getFechaActualizacion())
            //     .observaciones(solicitud.getObservaciones())
            //     .numeroDocumento(solicitud.getNumeroDocumento())
            //     .nombreCompleto(solicitud.getNombreCompleto())
            //     .timestamp(Instant.now())
            //     .eventId(UUID.randomUUID().toString())
            //     .version("1.0")
            //     .build();
            //
            // eventPublisher.publish("solicitudes.estado-changed", event);

            log.debug("✅ Evento CambioEstadoSolicitud publicado exitosamente para solicitud {}", solicitud.getId());

        } catch (Exception e) {
            log.error("❌ Error publicando evento CambioEstadoSolicitud para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
            // No lanzar excepción aquí para no interrumpir el flujo principal
            // El cambio de estado debe completarse aunque falle la publicación del evento
        }
    }

    @Override
    public void publicarEventoSolicitudAprobada(Solicitud solicitud) {
        try {
            log.info("✅ EVENTO PUBLICADO: SolicitudAprobada");
            log.info("   • ID Solicitud: {}", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Documento: {}", solicitud.getNumeroDocumento());
            log.info("   • Monto aprobado: ${:,.2f}", solicitud.getMontoSolicitado());
            log.info("   • Tipo de crédito: {}", solicitud.getTipoCredito());
            log.info("   • Fecha aprobación: {}", solicitud.getFechaActualizacion());

            // En un proyecto real, este evento podría disparar:
            // - Creación automática del contrato de préstamo
            // - Inicio del proceso de desembolso
            // - Actualización en sistemas de scoring crediticio
            // - Notificaciones a otros microservicios (contabilidad, riesgo, etc.)
            // - Registro en sistemas de auditoría
            // - Activación de seguros asociados al crédito

            // SolicitudAprobadaEvent event = SolicitudAprobadaEvent.builder()
            //     .solicitudId(solicitud.getId())
            //     .numeroDocumento(solicitud.getNumeroDocumento())
            //     .nombreCompleto(solicitud.getNombreCompleto())
            //     .email(solicitud.getEmail())
            //     .montoAprobado(solicitud.getMontoSolicitado())
            //     .tipoCredito(solicitud.getTipoCredito())
            //     .fechaAprobacion(solicitud.getFechaActualizacion())
            //     .plazoMeses(solicitud.getPlazoMeses())
            //     .tasaInteres(solicitud.getTasaInteresCalculada())
            //     .timestamp(Instant.now())
            //     .eventId(UUID.randomUUID().toString())
            //     .version("1.0")
            //     .build();
            //
            // eventPublisher.publish("solicitudes.approved", event);

            log.debug("✅ Evento SolicitudAprobada publicado exitosamente para solicitud {}", solicitud.getId());

        } catch (Exception e) {
            log.error("❌ Error publicando evento SolicitudAprobada para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
            // Este evento es crítico para otros procesos, pero no debe fallar la aprobación
        }
    }

    @Override
    public void publicarEventoSolicitudRechazada(Solicitud solicitud) {
        try {
            log.info("❌ EVENTO PUBLICADO: SolicitudRechazada");
            log.info("   • ID Solicitud: {}", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Documento: {}", solicitud.getNumeroDocumento());
            log.info("   • Monto solicitado: ${:,.2f}", solicitud.getMontoSolicitado());
            log.info("   • Motivo rechazo: {}", solicitud.getObservaciones());
            log.info("   • Fecha rechazo: {}", solicitud.getFechaActualizacion());

            // En un proyecto real, este evento podría:
            // - Actualizar historial crediticio del cliente
            // - Disparar análisis de mejora de algoritmos de pre-aprobación
            // - Generar reportes de causas de rechazo para business intelligence
            // - Iniciar procesos de re-engagement con el cliente
            // - Registrar métricas de rechazo por motivos
            // - Disparar campañas de productos alternativos

            // SolicitudRechazadaEvent event = SolicitudRechazadaEvent.builder()
            //     .solicitudId(solicitud.getId())
            //     .numeroDocumento(solicitud.getNumeroDocumento())
            //     .nombreCompleto(solicitud.getNombreCompleto())
            //     .email(solicitud.getEmail())
            //     .montoSolicitado(solicitud.getMontoSolicitado())
            //     .tipoCredito(solicitud.getTipoCredito())
            //     .motivoRechazo(solicitud.getObservaciones())
            //     .fechaRechazo(solicitud.getFechaActualizacion())
            //     .categoriaRechazo(determinarCategoriaRechazo(solicitud.getObservaciones()))
            //     .timestamp(Instant.now())
            //     .eventId(UUID.randomUUID().toString())
            //     .version("1.0")
            //     .build();
            //
            // eventPublisher.publish("solicitudes.rejected", event);

            log.debug("✅ Evento SolicitudRechazada publicado exitosamente para solicitud {}", solicitud.getId());

        } catch (Exception e) {
            log.error("❌ Error publicando evento SolicitudRechazada para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
            // Este evento es importante para analytics pero no debe fallar el rechazo
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ============================================================

    /**
     * Determina la categoría del rechazo basado en las observaciones
     * Útil para análisis y reportes de business intelligence
     */
    private String determinarCategoriaRechazo(String observaciones) {
        if (observaciones == null || observaciones.trim().isEmpty()) {
            return "OTROS";
        }

        String obs = observaciones.toLowerCase();

        if (obs.contains("score") || obs.contains("crediticio")) {
            return "HISTORIAL_CREDITICIO";
        } else if (obs.contains("ingreso") || obs.contains("capacidad de pago")) {
            return "INGRESOS_INSUFICIENTES";
        } else if (obs.contains("documento") || obs.contains("identidad")) {
            return "DOCUMENTACION";
        } else if (obs.contains("edad") || obs.contains("menor")) {
            return "EDAD";
        } else if (obs.contains("lista") || obs.contains("restrictiva")) {
            return "LISTAS_RESTRICTIVAS";
        } else {
            return "OTROS";
        }
    }

    /**
     * Método para publicar evento con información del estado anterior
     * Útil cuando se tiene contexto del cambio de estado
     */
    public void publicarCambioEstadoConHistorial(Solicitud solicitud, EstadoSolicitud estadoAnterior) {
        try {
            log.info("🔄 EVENTO PUBLICADO: CambioEstadoSolicitudConHistorial");
            log.info("   • ID Solicitud: {}", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Estado anterior: {}", estadoAnterior != null ? estadoAnterior.getDescripcion() : "N/A");
            log.info("   • Estado nuevo: {}", solicitud.getEstado().getDescripcion());
            log.info("   • Fecha actualización: {}", solicitud.getFechaActualizacion());

            // Este método adicional es útil para casos donde se necesita
            // el contexto completo del cambio de estado

        } catch (Exception e) {
            log.error("❌ Error publicando evento CambioEstadoConHistorial para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
        }
    }
}