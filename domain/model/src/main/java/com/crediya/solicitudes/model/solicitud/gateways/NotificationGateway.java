// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/NotificationGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;

/**
 * Gateway para envío de notificaciones
 */
public interface NotificationGateway {

    /**
     * Notifica cuando se crea una nueva solicitud
     * @param solicitud la solicitud creada
     */
    void notificarSolicitudCreada(Solicitud solicitud);

    /**
     * Notifica cuando cambia el estado de una solicitud
     * @param solicitud la solicitud actualizada
     */
    void notificarCambioEstado(Solicitud solicitud);

    /**
     * Notifica cuando una solicitud es aprobada
     * @param solicitud la solicitud aprobada
     */
    void notificarSolicitudAprobada(Solicitud solicitud);

    /**
     * Notifica cuando una solicitud es rechazada
     * @param solicitud la solicitud rechazada
     */
    void notificarSolicitudRechazada(Solicitud solicitud);
}