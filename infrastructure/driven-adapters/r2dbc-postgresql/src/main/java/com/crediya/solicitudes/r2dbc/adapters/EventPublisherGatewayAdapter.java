// infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/com/crediya/solicitudes/r2dbc/adapters/EventPublisherGatewayAdapter.java
package com.crediya.solicitudes.r2dbc.adapters;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.gateways.EventPublisherGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Implementación MOCK reactiva del EventPublisherGateway
 * En producción se reemplazaría por integración real con sistemas de eventos (SQS, Kafka, etc.)
 */
@Slf4j
@Component
public class EventPublisherGatewayAdapter implements EventPublisherGateway {

    @Override
    public Mono<Void> publicarEventoSolicitudCreada(Solicitud solicitud) {
        return Mono.fromRunnable(() -> {
                    log.info("🚀 EVENTO PUBLICADO (MOCK):");
                    log.info("   • Evento: SolicitudCreada");
                    log.info("   • ID Solicitud: {}", solicitud.getId());
                    log.info("   • Documento: {}", solicitud.getNumeroDocumento());
                    log.info("   • Estado: {}", solicitud.getEstado());
                    log.info("   • Timestamp: {}", LocalDateTime.now());
                    log.info("   • Destino: Cola de eventos para procesamiento posterior");
                })
                .doOnSuccess(unused -> log.debug("✅ Evento publicado correctamente"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error publicando evento (no crítico): {}", ex.getMessage());
                    return Mono.empty(); // No fallar el flujo principal por error en eventos
                }).then();
    }

    @Override
    public Mono<Void> publicarEventoCambioEstado(Solicitud solicitud) {
        return Mono.fromRunnable(() -> {
                    log.info("🔄 EVENTO CAMBIO ESTADO (MOCK): {} -> {}",
                            solicitud.getId(), solicitud.getEstado());
                })
                .doOnSuccess(unused -> log.debug("✅ Evento cambio estado publicado"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error publicando evento cambio estado: {}", ex.getMessage());
                    return Mono.empty();
                }).then();
    }

    @Override
    public Mono<Void> publicarEventoSolicitudAprobada(Solicitud solicitud) {
        return Mono.fromRunnable(() -> {
                    log.info("✅ EVENTO SOLICITUD APROBADA (MOCK): {}", solicitud.getId());
                })
                .doOnSuccess(unused -> log.debug("✅ Evento aprobación publicado"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error publicando evento aprobación: {}", ex.getMessage());
                    return Mono.empty();
                }).then();
    }

    @Override
    public Mono<Void> publicarEventoSolicitudRechazada(Solicitud solicitud) {
        return Mono.fromRunnable(() -> {
                    log.info("❌ EVENTO SOLICITUD RECHAZADA (MOCK): {}", solicitud.getId());
                })
                .doOnSuccess(unused -> log.debug("✅ Evento rechazo publicado"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error publicando evento rechazo: {}", ex.getMessage());
                    return Mono.empty();
                }).then();
    }
}