// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/exception/DatosSolicitudInvalidosExceptionTest.java
package com.crediya.solicitudes.model.solicitud.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DatosSolicitudInvalidosException")
class DatosSolicitudInvalidosExceptionTest {

    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void debeCrearExcepcionConMensaje() {
        // Given
        String mensaje = "Los datos de la solicitud son inválidos";

        // When
        DatosSolicitudInvalidosException excepcion =
                new DatosSolicitudInvalidosException(mensaje);

        // Then
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Debe crear excepción con mensaje y causa")
    void debeCrearExcepcionConMensajeYCausa() {
        // Given
        String mensaje = "Error de validación";
        Throwable causa = new IllegalArgumentException("Argumento inválido");

        // When
        DatosSolicitudInvalidosException excepcion =
                new DatosSolicitudInvalidosException(mensaje, causa);

        // Then
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion.getCause()).isEqualTo(causa);
    }

    @Test
    @DisplayName("Debe ser lanzada y capturada correctamente")
    void debeSerLanzadaYCapturadaCorrectamente() {
        // Given
        String mensaje = "Documento inválido";

        // When & Then
        assertThatThrownBy(() -> {
            throw new DatosSolicitudInvalidosException(mensaje);
        })
                .isInstanceOf(DatosSolicitudInvalidosException.class)
                .hasMessage(mensaje);
    }
}