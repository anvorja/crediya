// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/EstadoSolicitud.java
package com.crediya.solicitudes.model.solicitud;

public enum EstadoSolicitud {
    PENDIENTE_REVISION("Pendiente de revisión"),
    PRE_APROBADA("Pre-aprobada automáticamente"),
    APROBADA("Aprobada por administrador"),
    RECHAZADA("Rechazada");

    private final String descripcion;

    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
        return switch (this) {
            case PENDIENTE_REVISION -> nuevoEstado == PRE_APROBADA ||
                    nuevoEstado == APROBADA ||
                    nuevoEstado == RECHAZADA;
            case PRE_APROBADA -> nuevoEstado == APROBADA ||
                    nuevoEstado == RECHAZADA;
            case APROBADA, RECHAZADA -> false; // Estados finales
        };
    }
}