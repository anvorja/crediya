// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/valueobjects/HistorialCrediticio.java
package com.crediya.solicitudes.model.solicitud.valueobjects;

import java.math.BigDecimal;

/**
 * Value Object que representa el historial crediticio completo de una persona
 * obtenido de centrales de riesgo crediticio
 */
public record HistorialCrediticio(
        int puntajeCrediticio,
        boolean tieneReportesNegativos,
        int creditosActivos,
        BigDecimal totalDeudas,
        String observaciones
) {
    public HistorialCrediticio {
        if (puntajeCrediticio < 300 || puntajeCrediticio > 850) {
            throw new IllegalArgumentException("Puntaje crediticio debe estar entre 300 y 850");
        }
        if (creditosActivos < 0) {
            throw new IllegalArgumentException("Número de créditos activos no puede ser negativo");
        }
        if (totalDeudas == null) {
            totalDeudas = BigDecimal.ZERO;
        }
        if (totalDeudas.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total de deudas no puede ser negativo");
        }
        if (observaciones == null) {
            observaciones = "";
        }
    }

    /**
     * Constructor simplificado para casos básicos
     */
    public HistorialCrediticio(boolean tieneReportesNegativos, int puntajeCrediticio) {
        this(puntajeCrediticio, tieneReportesNegativos, 0, BigDecimal.ZERO, "");
    }

    /**
     * Determina si el historial es favorable para otorgar crédito
     */
    public boolean esFavorable() {
        return puntajeCrediticio >= 650 && !tieneReportesNegativos;
    }

    /**
     * Obtiene la clasificación del puntaje crediticio
     */
    public ClasificacionCrediticia getClasificacion() {
        if (puntajeCrediticio >= 750) return ClasificacionCrediticia.EXCELENTE;
        if (puntajeCrediticio >= 650) return ClasificacionCrediticia.BUENO;
        if (puntajeCrediticio >= 550) return ClasificacionCrediticia.REGULAR;
        if (puntajeCrediticio >= 450) return ClasificacionCrediticia.MALO;
        return ClasificacionCrediticia.MUY_MALO;
    }

    public enum ClasificacionCrediticia {
        EXCELENTE("Excelente"),
        BUENO("Bueno"),
        REGULAR("Regular"),
        MALO("Malo"),
        MUY_MALO("Muy Malo");

        private final String descripcion;

        ClasificacionCrediticia(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}