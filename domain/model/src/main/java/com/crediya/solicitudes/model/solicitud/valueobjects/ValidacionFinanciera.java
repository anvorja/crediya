// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/valueobjects/ValidacionFinanciera.java
package com.crediya.solicitudes.model.solicitud.valueobjects;

import java.math.BigDecimal;

/**
 * Value Object que representa la validación de información financiera
 * obtenida de fuentes externas como DIAN, PILA, sistemas tributarios, etc.
 */
public record ValidacionFinanciera(
        boolean ingresosVerificados,
        BigDecimal ingresosProbados,
        String fuenteValidacion,
        String observaciones
) {
    public ValidacionFinanciera {
        if (ingresosProbados == null) {
            ingresosProbados = BigDecimal.ZERO;
        }
        if (ingresosProbados.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Los ingresos probados no pueden ser negativos");
        }
        if (fuenteValidacion == null || fuenteValidacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La fuente de validación no puede estar vacía");
        }
        if (observaciones == null) {
            observaciones = "";
        }
    }

    /**
     * Calcula el porcentaje de variación entre ingresos declarados y probados
     */
    public double calcularVariacionPorcentaje(BigDecimal ingresosDeclarados) {
        if (ingresosDeclarados == null || ingresosDeclarados.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        if (!ingresosVerificados || ingresosProbados.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        return ingresosProbados.subtract(ingresosDeclarados)
                .divide(ingresosDeclarados, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    /**
     * Determina si la diferencia entre ingresos es aceptable (< 20%)
     */
    public boolean esVariacionAceptable(BigDecimal ingresosDeclarados) {
        double variacion = Math.abs(calcularVariacionPorcentaje(ingresosDeclarados));
        return variacion <= 20.0;
    }

    public boolean tieneObservaciones() {
        return observaciones != null && !observaciones.trim().isEmpty();
    }
}