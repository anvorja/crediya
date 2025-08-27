// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/exception/SolicitudDuplicadaExceptionTest.java
package com.crediya.solicitudes.model.solicitud.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SolicitudDuplicadaException")
class SolicitudDuplicadaExceptionTest {

    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void debeCrearExcepcionConMensaje() {
        // Given
        String mensaje = "Ya existe una solicitud para este documento";

        // When
        SolicitudDuplicadaException excepcion =
                new SolicitudDuplicadaException(mensaje);

        // Then
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Debe crear excepción con mensaje y causa")
    void debeCrearExcepcionConMensajeYCausa() {
        // Given
        String mensaje = "Solicitud duplicada detectada";
        Throwable causa = new IllegalStateException("Estado inconsistente");

        // When
        SolicitudDuplicadaException excepcion =
                new SolicitudDuplicadaException(mensaje, causa);

        // Then
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion.getCause()).isEqualTo(causa);
    }

    @Test
    @DisplayName("Debe ser lanzada y capturada correctamente")
    void debeSerLanzadaYCapturadaCorrectamente() {
        // Given
        String documento = "12345678";
        String mensaje = "Ya existe una solicitud activa para el documento: " + documento;

        // When & Then
        assertThatThrownBy(() -> {
            throw new SolicitudDuplicadaException(mensaje);
        })
                .isInstanceOf(SolicitudDuplicadaException.class)
                .hasMessage(mensaje);
    }
}