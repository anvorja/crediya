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
 */
@Slf4j
@Component
public class EventPublisherGatewayAdapter implements EventPublisherGateway {

    @Override
    public void publicarSolicitudCreada(Solicitud solicitud) {
        try {
            log.info("🚀 EVENTO PUBLICADO: SolicitudCreada");
            log.info("   • ID Solicitud: {}", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Documento: {}", solicitud.getNumeroDocumento());
            log.info("   • Estado inicial: {}", solicitud.getEstado());
            log.info("   • Monto: ${:,.2f}", solicitud.getMontoSolicitado());
            log.info("   • Fecha: {}", solicitud.getFechaCreacion());

            // En un proyecto real aquí se construiría y publicaría el evento:
            // SolicitudCreadaEvent event = SolicitudCreadaEvent.builder()
            //     .solicitudId(solicitud.getId())
            //     .numeroDocumento(solicitud.getNumeroDocumento())
            //     .nombreCompleto(solicitud.getNombreCompleto())
            //     .estado(solicitud.getEstado().name())
            //     .monto(solicitud.getMontoSolicitado())
            //     .tipoCredito(solicitud.getTipoCredito())
            //     .fechaCreacion(solicitud.getFechaCreacion())
            //     .build();
            //
            // eventPublisher.publish("solicitudes.created", event);

        } catch (Exception e) {
            log.error("Error publicando evento SolicitudCreada para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
            // En producción, podríamos:
            // - Reintentar la publicación
            // - Guardar en dead letter queue
            // - Alertar sobre fallos de eventos
        }
    }

    @Override
    public void publicarCambioEstadoSolicitud(Solicitud solicitud, EstadoSolicitud estadoAnterior) {
        try {
            log.info("🔄 EVENTO PUBLICADO: CambioEstadoSolicitud");
            log.info("   • ID Solicitud: {}", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Estado anterior: {}", estadoAnterior);
            log.info("   • Estado actual: {}", solicitud.getEstado());
            log.info("   • Fecha actualización: {}", solicitud.getFechaActualizacion());
            log.info("   • Observaciones: {}", solicitud.getObservaciones());

            // En un proyecto real:
            // CambioEstadoEvent event = CambioEstadoEvent.builder()
            //     .solicitudId(solicitud.getId())
            //     .estadoAnterior(estadoAnterior.name())
            //     .estadoNuevo(solicitud.getEstado().name())
            //     .fechaCambio(solicitud.getFechaActualizacion())
            //     .observaciones(solicitud.getObservaciones())
            //     .build();
            //
            // eventPublisher.publish("solicitudes.estado-changed", event);

        } catch (Exception e) {
            log.error("Error publicando evento CambioEstadoSolicitud para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
        }
    }

    @Override
    public void publicarSolicitudAprobada(Solicitud solicitud) {
        try {
            log.info("✅ EVENTO PUBLICADO: SolicitudAprobada");
            log.info("   • ID Solicitud: {}", solicitud.getId());
            log.info("   • Cliente: {}", solicitud.getNombreCompleto());
            log.info("   • Documento: {}", solicitud.getNumeroDocumento());
            log.info("   • Monto aprobado: ${:,.2f}", solicitud.getMontoSolicitado());
            log.info("   • Tipo de crédito: {}", solicitud.getTipoCredito());
            log.info("   • Fecha aprobación: {}", solicitud.getFechaActualizacion());

            // En un proyecto real, este evento podría disparar:
            // - Creación automática del contrato
            // - Inicio del proceso de desembolso
            // - Actualización en sistemas de scoring
            // - Notificaciones a otros microservicios

            // SolicitudAprobadaEvent event = SolicitudAprobadaEvent.builder()
            //     .solicitudId(solicitud.getId())
            //     .numeroDocumento(solicitud.getNumeroDocumento())
            //     .nombreCompleto(solicitud.getNombreCompleto())
            //     .montoAprobado(solicitud.getMontoSolicitado())
            //     .tipoCredito(solicitud.getTipoCredito())
            //     .fechaAprobacion(solicitud.getFechaActualizacion())
            //     .build();
            //
            // eventPublisher.publish("solicitudes.approved", event);

        } catch (Exception e) {
            log.error("Error publicando evento SolicitudAprobada para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
        }
    }

    @Override
    public void publicarSolicitudRechazada(Solicitud solicitud) {
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
            // - Generar reportes de causas de rechazo
            // - Iniciar procesos de re-engagement con el cliente

            // SolicitudRechazadaEvent event = SolicitudRechazadaEvent.builder()
            //     .solicitudId(solicitud.getId())
            //     .numeroDocumento(solicitud.getNumeroDocumento())
            //     .nombreCompleto(solicitud.getNombreCompleto())
            //     .montoSolicitado(solicitud.getMontoSolicitado())
            //     .motivoRechazo(solicitud.getObservaciones())
            //     .fechaRechazo(solicitud.getFechaActualizacion())
            //     .build();
            //
            // eventPublisher.publish("solicitudes.rejected", event);

        } catch (Exception e) {
            log.error("Error publicando evento SolicitudRechazada para solicitud {}: {}",
                    solicitud.getId(), e.getMessage());
        }
    }
}