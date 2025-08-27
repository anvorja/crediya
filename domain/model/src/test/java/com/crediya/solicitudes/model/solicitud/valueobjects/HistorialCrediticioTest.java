// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/valueobjects/HistorialCrediticioTest.java
package com.crediya.solicitudes.model.solicitud.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("HistorialCrediticio - Value Object")
class HistorialCrediticioTest {

    @Test
    @DisplayName("Debe crear historial crediticio con reportes negativos")
    void debeCrearHistorialConReportesNegativos() {
        // When
        HistorialCrediticio historial = new HistorialCrediticio(true, 450);

        // Then
        assertThat(historial.tieneReportesNegativos()).isTrue();
        assertThat(historial.puntajeCrediticio()).isEqualTo(450);
    }

    @Test
    @DisplayName("Debe crear historial crediticio limpio")
    void debeCrearHistorialLimpio() {
        // When
        HistorialCrediticio historial = new HistorialCrediticio(false, 750);

        // Then
        assertThat(historial.tieneReportesNegativos()).isFalse();
        assertThat(historial.puntajeCrediticio()).isEqualTo(750);
    }

    @Test
    @DisplayName("Debe validar puntaje crediticio en rango válido")
    void debeValidarPuntajeEnRango() {
        // Given & When & Then
        assertThatNoException().isThrownBy(() -> new HistorialCrediticio(false, 300));
        assertThatNoException().isThrownBy(() -> new HistorialCrediticio(false, 850));
    }

    @Test
    @DisplayName("Debe rechazar puntaje crediticio fuera de rango")
    void debeRechazarPuntajeFueraDeRango() {
        // When & Then
        assertThatThrownBy(() -> new HistorialCrediticio(false, 299))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Puntaje crediticio debe estar entre 300 y 850");

        assertThatThrownBy(() -> new HistorialCrediticio(false, 851))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Puntaje crediticio debe estar entre 300 y 850");
    }

    @Test
    @DisplayName("Debe comparar historiales por igualdad")
    void debeCompararHistorialesPorIgualdad() {
        // Given
        HistorialCrediticio historial1 = new HistorialCrediticio(true, 500);
        HistorialCrediticio historial2 = new HistorialCrediticio(true, 500);
        HistorialCrediticio historial3 = new HistorialCrediticio(false, 500);

        // Then
        assertThat(historial1).isEqualTo(historial2);
        assertThat(historial1).isNotEqualTo(historial3);
        assertThat(historial1.hashCode()).isEqualTo(historial2.hashCode());
    }
}