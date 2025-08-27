// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/EventPublisherGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;

/**
 * Gateway para publicación de eventos
 */
public interface EventPublisherGateway {

    /**
     * Publica evento cuando se crea una solicitud
     * @param solicitud la solicitud creada
     */
    void publicarEventoSolicitudCreada(Solicitud solicitud);

    /**
     * Publica evento cuando cambia el estado de una solicitud
     * @param solicitud la solicitud actualizada
     */
    void publicarEventoCambioEstado(Solicitud solicitud);

    /**
     * Publica evento cuando una solicitud es aprobada
     * @param solicitud la solicitud aprobada
     */
    void publicarEventoSolicitudAprobada(Solicitud solicitud);

    /**
     * Publica evento cuando una solicitud es rechazada
     * @param solicitud la solicitud rechazada
     */
    void publicarEventoSolicitudRechazada(Solicitud solicitud);
}