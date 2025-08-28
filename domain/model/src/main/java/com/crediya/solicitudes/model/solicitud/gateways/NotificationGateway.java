// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/NotificationGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

/**
 * Gateway REACTIVO para envío de notificaciones
 */
public interface NotificationGateway {


    /**
     * Notifica cuando se crea una nueva solicitud de forma reactiva
     * @param solicitud la solicitud creada
     * @return Mono<Void> que se completa cuando la notificación es enviada
     */
    Mono<Void> notificarSolicitudCreada(Solicitud solicitud);

    // ========================================
    // MÉTODOS OPCIONALES PARA FUNCIONALIDADES FUTURAS
    // ========================================

    /**
     * Notifica cambio de estado de forma reactiva
     * @param solicitud la solicitud actualizada
     * @return Mono<Void> que se completa cuando la notificación es enviada
     */
    default Mono<Void> notificarCambioEstado(Solicitud solicitud) {
        // Implementación por defecto - los adapters pueden sobrescribirla
        return Mono.empty();
    }

    /**
     * Notifica cuando una solicitud requiere revisión manual de forma reactiva
     * @param solicitud la solicitud que requiere revisión
     * @return Mono<Void> que se completa cuando la notificación es enviada
     */
    default Mono<Void> notificarRevisionManual(Solicitud solicitud) {
        // Implementación por defecto - los adapters pueden sobrescribirla
        return Mono.empty();
    }
}