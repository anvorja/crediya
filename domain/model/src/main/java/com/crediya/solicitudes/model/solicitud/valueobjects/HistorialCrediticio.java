package com.crediya.solicitudes.model.solicitud.valueobjects;

/**
 * Value Object que representa el historial crediticio de una persona
 */
public record HistorialCrediticio(
        boolean tieneReportesNegativos,
        int puntajeCrediticio
) {

    public HistorialCrediticio {
        if (puntajeCrediticio < 300 || puntajeCrediticio > 850) {
            throw new IllegalArgumentException("Puntaje crediticio debe estar entre 300 y 850");
        }
    }

    public boolean tieneReportesNegativos() {
        return tieneReportesNegativos;
    }

    public int puntajeCrediticio() {
        return puntajeCrediticio;
    }
}