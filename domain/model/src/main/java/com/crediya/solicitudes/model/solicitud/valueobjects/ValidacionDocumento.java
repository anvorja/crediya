// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/valueobjects/ValidacionDocumento.java
package com.crediya.solicitudes.model.solicitud.valueobjects;

/**
 * Value Object que representa el resultado de una validación de documento de identidad
 * con información obtenida de registros oficiales
 */
public record ValidacionDocumento(
        boolean esValido,
        String nombre,
        String apellido,
        String observaciones
) {
    public ValidacionDocumento {
        if (nombre == null || apellido == null) {
            throw new IllegalArgumentException("Nombre y apellido no pueden ser null");
        }
        if (observaciones == null) {
            observaciones = "";
        }
    }

    /**
     * Constructor simplificado para validaciones básicas
     */
    public ValidacionDocumento(boolean esValido, String nombre, String apellido) {
        this(esValido, nombre, apellido, "");
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

    public String observaciones() {
        return observaciones;
    }

    public boolean tieneObservaciones() {
        return observaciones != null && !observaciones.trim().isEmpty();
    }
}