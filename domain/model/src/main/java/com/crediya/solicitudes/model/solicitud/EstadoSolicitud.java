// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/EstadoSolicitud.java
package com.crediya.solicitudes.model.solicitud;

import java.util.Set;
import java.util.EnumSet;

/**
 * Estados posibles de una solicitud de crédito
 */
public enum EstadoSolicitud {
    PENDIENTE_REVISION("Pendiente de Revisión", false, true),
    EN_REVISION("En Revisión", false, false),
    APROBADA("Aprobada", true, false),
    RECHAZADA("Rechazada", true, false);

    private final String descripcion;
    private final boolean esFinal;
    private final boolean esEditable;

    EstadoSolicitud(String descripcion, boolean esFinal, boolean esEditable) {
        this.descripcion = descripcion;
        this.esFinal = esFinal;
        this.esEditable = esEditable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esFinal() {
        return esFinal;
    }

    public boolean esEditable() {
        return esEditable;
    }

    public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
        if (this.esFinal) {
            return false; // Estados finales no pueden cambiar
        }

        if (this == nuevoEstado) {
            return false; // No se puede transicionar al mismo estado
        }

        return switch (this) {
            case PENDIENTE_REVISION -> nuevoEstado == EN_REVISION ||
                    nuevoEstado == APROBADA ||
                    nuevoEstado == RECHAZADA;
            case EN_REVISION -> nuevoEstado == APROBADA ||
                    nuevoEstado == RECHAZADA;
            default -> false;
        };
    }

    public Set<EstadoSolicitud> obtenerEstadosSiguientesValidos() {
        return switch (this) {
            case PENDIENTE_REVISION -> EnumSet.of(EN_REVISION, APROBADA, RECHAZADA);
            case EN_REVISION -> EnumSet.of(APROBADA, RECHAZADA);
            case APROBADA, RECHAZADA -> EnumSet.noneOf(EstadoSolicitud.class);
        };
    }

    public static EstadoSolicitud getEstadoInicial() {
        return PENDIENTE_REVISION;
    }

    public static EstadoSolicitud fromString(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado no puede ser null o vacío");
        }

        try {
            return valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: " + estado, e);
        }
    }

    public static Set<EstadoSolicitud> getEstadosNoFinales() {
        return EnumSet.of(PENDIENTE_REVISION, EN_REVISION);
    }

    public static Set<EstadoSolicitud> getEstadosFinales() {
        return EnumSet.of(APROBADA, RECHAZADA);
    }
}