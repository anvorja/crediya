// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/valueobjects/ValidacionDocumentoTest.java
package com.crediya.solicitudes.model.solicitud.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("ValidacionDocumento - Value Object")
class ValidacionDocumentoTest {

    @Test
    @DisplayName("Debe crear validación documento correctamente")
    void debeCrearValidacionDocumento() {
        // When
        ValidacionDocumento validacion = new ValidacionDocumento(true, "Juan", "Pérez");

        // Then
        assertThat(validacion.esValido()).isTrue();
        assertThat(validacion.nombre()).isEqualTo("Juan");
        assertThat(validacion.apellido()).isEqualTo("Pérez");
    }

    @Test
    @DisplayName("Debe manejar documento inválido")
    void debeManejarDocumentoInvalido() {
        // When
        ValidacionDocumento validacion = new ValidacionDocumento(false, "Pedro", "García");

        // Then
        assertThat(validacion.esValido()).isFalse();
        assertThat(validacion.nombre()).isEqualTo("Pedro");
        assertThat(validacion.apellido()).isEqualTo("García");
    }

    @Test
    @DisplayName("Debe comparar validaciones por igualdad")
    void debeCompararValidacionesPorIgualdad() {
        // Given
        ValidacionDocumento validacion1 = new ValidacionDocumento(true, "Juan", "Pérez");
        ValidacionDocumento validacion2 = new ValidacionDocumento(true, "Juan", "Pérez");
        ValidacionDocumento validacion3 = new ValidacionDocumento(false, "Juan", "Pérez");

        // Then
        assertThat(validacion1).isEqualTo(validacion2);
        assertThat(validacion1).isNotEqualTo(validacion3);
        assertThat(validacion1.hashCode()).isEqualTo(validacion2.hashCode());
    }
}