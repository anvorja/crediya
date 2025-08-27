// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/exception/SolicitudNoEncontradaExceptionTest.java
package com.crediya.solicitudes.model.solicitud.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SolicitudNoEncontradaException")
class SolicitudNoEncontradaExceptionTest {

    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void debeCrearExcepcionConMensaje() {
        // Given
        String mensaje = "Solicitud no encontrada";

        // When
        SolicitudNoEncontradaException excepcion =
                new SolicitudNoEncontradaException(mensaje);

        // Then
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Debe crear excepción con mensaje y causa")
    void debeCrearExcepcionConMensajeYCausa() {
        // Given
        String mensaje = "No se pudo encontrar la solicitud";
        Throwable causa = new RuntimeException("Error de base de datos");

        // When
        SolicitudNoEncontradaException excepcion =
                new SolicitudNoEncontradaException(mensaje, causa);

        // Then
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion.getCause()).isEqualTo(causa);
    }

    @Test
    @DisplayName("Debe ser lanzada y capturada correctamente")
    void debeSerLanzadaYCapturadaCorrectamente() {
        // Given
        String id = "SOL-001";
        String mensaje = "Solicitud no encontrada con ID: " + id;

        // When & Then
        assertThatThrownBy(() -> {
            throw new SolicitudNoEncontradaException(mensaje);
        })
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessage(mensaje);
    }
}
