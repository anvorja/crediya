// infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/com/crediya/solicitudes/r2dbc/adapters/NotificationGatewayAdapter.java
package com.crediya.solicitudes.r2dbc.adapters;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.gateways.NotificationGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implementación MOCK reactiva del NotificationGateway
 * En producción se reemplazaría por integración real con servicios de notificación
 */
@Slf4j
@Component
public class NotificationGatewayAdapter implements NotificationGateway {

    @Override
    public Mono<Void> notificarSolicitudCreada(Solicitud solicitud) {
        return Mono.fromRunnable(() -> {
                    log.info("📧 NOTIFICACIÓN ENVIADA (MOCK):");
                    log.info("   • Cliente: {} ({})",
                            solicitud.getNombres() + " " + solicitud.getApellidos(),
                            solicitud.getEmail());
                    log.info("   • Solicitud: {} - Estado: {}",
                            solicitud.getId(), solicitud.getEstado());
                    log.info("   • Monto: ${:,.2f} - Tipo: {}",
                            solicitud.getMontoSolicitado(), solicitud.getTipoCredito());
                    log.info("   • Mensaje: Su solicitud ha sido registrada exitosamente");
                })
                .doOnSuccess(unused -> log.debug("✅ Notificación procesada correctamente"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error en notificación (no crítico): {}", ex.getMessage());
                    return Mono.empty(); // No fallar el flujo principal por error en notificación
                }).then();
    }
}