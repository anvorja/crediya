package com.crediya.solicitudes.model.solicitud.valueobjects;

/**
 * Value Object que representa el resultado de una validación de documento
 */
public record ValidacionDocumento(
        boolean esValido,
        String nombre,
        String apellido
) {

    public ValidacionDocumento {
        if (nombre == null || apellido == null) {
            throw new IllegalArgumentException("Nombre y apellido no pueden ser null");
        }
    }

    public boolean esValido() {
        return esValido;
    }

    public String nombre() {
        return nombre;
    }

    public String apellido() {
        return apellido;
    }
}