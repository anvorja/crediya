// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/NotificationGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.enums.TipoNotificacion;

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
     * Notifica cambio de estado con tipo específico
     * @param solicitud la solicitud actualizada
     * @param tipo tipo de notificación a enviar
     * @return true si se envió correctamente
     */
    boolean notificarEstadoSolicitud(Solicitud solicitud, TipoNotificacion tipo);

    /**
     * Notifica a administradores sobre nueva solicitud
     * @param solicitud la solicitud creada
     * @return true si se envió correctamente
     */
    boolean notificarNuevaSolicitudAAdministradores(Solicitud solicitud);

    /**
     * Notifica cuando una solicitud requiere revisión manual
     * @param solicitud la solicitud que requiere revisión
     * @return true si se envió correctamente
     */
    boolean notificarSolicitudRequiereRevision(Solicitud solicitud);
}