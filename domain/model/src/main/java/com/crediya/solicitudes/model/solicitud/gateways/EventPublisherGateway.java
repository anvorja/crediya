// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/EventPublisherGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;

/**
 * Gateway para publicación de eventos (Puerto Secundario)
 */
public interface EventPublisherGateway {

    /**
     * Publica evento cuando se crea una nueva solicitud
     */
    void publicarSolicitudCreada(Solicitud solicitud);

    /**
     * Publica evento cuando cambia el estado de una solicitud
     */
    void publicarCambioEstadoSolicitud(Solicitud solicitud, EstadoSolicitud estadoAnterior);

    /**
     * Publica evento cuando una solicitud es aprobada
     */
    void publicarSolicitudAprobada(Solicitud solicitud);

    /**
     * Publica evento cuando una solicitud es rechazada
     */
    void publicarSolicitudRechazada(Solicitud solicitud);
}