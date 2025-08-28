// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/EventPublisherGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

/**
 * Gateway REACTIVO para publicación de eventos
 */
public interface EventPublisherGateway {

    /**
     * Publica evento cuando se crea una solicitud de forma reactiva
     * @param solicitud la solicitud creada
     * @return Mono<Void> que se completa cuando el evento es publicado
     */
    Mono<Void> publicarEventoSolicitudCreada(Solicitud solicitud);

    /**
     * Publica evento cuando cambia el estado de una solicitud de forma reactiva
     * @param solicitud la solicitud actualizada
     * @return Mono<Void> que se completa cuando el evento es publicado
     */
    Mono<Void> publicarEventoCambioEstado(Solicitud solicitud);

    /**
     * Publica evento cuando una solicitud es aprobada de forma reactiva
     * @param solicitud la solicitud aprobada
     * @return Mono<Void> que se completa cuando el evento es publicado
     */
    Mono<Void> publicarEventoSolicitudAprobada(Solicitud solicitud);

    /**
     * Publica evento cuando una solicitud es rechazada de forma reactiva
     * @param solicitud la solicitud rechazada
     * @return Mono<Void> que se completa cuando el evento es publicado
     */
    Mono<Void> publicarEventoSolicitudRechazada(Solicitud solicitud);
}