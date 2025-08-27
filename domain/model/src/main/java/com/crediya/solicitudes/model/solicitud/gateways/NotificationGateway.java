// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/NotificationGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;

/**
 * Gateway para el envío de notificaciones (Puerto Secundario)
 * DOMINIO PURO: Sin dependencias de Reactor
 */
public interface NotificationGateway {

    /**
     * Notifica al solicitante sobre el estado de su solicitud
     */
    boolean notificarEstadoSolicitud(Solicitud solicitud, TipoNotificacion tipo);

    /**
     * Notifica a los administradores sobre una nueva solicitud
     */
    boolean notificarNuevaSolicitudAAdministradores(Solicitud solicitud);

    /**
     * Notifica cuando una solicitud requiere revisión manual
     */
    boolean notificarSolicitudRequiereRevision(Solicitud solicitud);

    enum TipoNotificacion {
        EMAIL, SMS, PUSH, TODOS
    }
}