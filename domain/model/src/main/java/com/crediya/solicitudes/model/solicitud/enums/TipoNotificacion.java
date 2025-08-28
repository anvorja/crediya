package com.crediya.solicitudes.model.solicitud.enums;

/**
 * Tipos de notificación disponibles en el sistema
 */
public enum TipoNotificacion {
    EMAIL("Correo electrónico"),
    SMS("Mensaje de texto"),
    PUSH("Notificación push"),
    TODOS("Todos los canales");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}