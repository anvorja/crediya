// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/exception/TransicionEstadoInvalidaExceptionTest.java
package com.crediya.solicitudes.model.solicitud.exception;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TransicionEstadoInvalidaException")
class TransicionEstadoInvalidaExceptionTest {

    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void debeCrearExcepcionConMensaje() {
        // Given
        String mensaje = "Transición de estado inválida";

        // When
        TransicionEstadoInvalidaException excepcion =
                new TransicionEstadoInvalidaException(mensaje);

        // Then
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Debe crear excepción con mensaje y causa")
    void debeCrearExcepcionConMensajeYCausa() {
        // Given
        String mensaje = "No se puede realizar la transición";
        Throwable causa = new IllegalStateException("Estado inconsistente");

        // When
        TransicionEstadoInvalidaException excepcion =
                new TransicionEstadoInvalidaException(mensaje, causa);

        // Then
        assertThat(excepcion.getMessage()).isEqualTo(mensaje);
        assertThat(excepcion.getCause()).isEqualTo(causa);
    }

    @Test
    @DisplayName("Debe crear mensaje descriptivo para transición inválida")
    void debeCrearMensajeDescriptivoParaTransicionInvalida() {
        // Given
        EstadoSolicitud desde = EstadoSolicitud.APROBADA;
        EstadoSolicitud hacia = EstadoSolicitud.PENDIENTE_REVISION;
        String mensaje = String.format("No se puede cambiar de %s a %s", desde, hacia);

        // When
        TransicionEstadoInvalidaException excepcion =
                new TransicionEstadoInvalidaException(mensaje);

        // Then
        assertThat(excepcion.getMessage())
                .contains("APROBADA")
                .contains("PENDIENTE_REVISION")
                .contains("No se puede cambiar");
    }

    @Test
    @DisplayName("Debe ser lanzada y capturada correctamente")
    void debeSerLanzadaYCapturadaCorrectamente() {
        // Given
        String mensaje = "Transición no permitida desde estado final";

        // When & Then
        assertThatThrownBy(() -> {
            throw new TransicionEstadoInvalidaException(mensaje);
        })
                .isInstanceOf(TransicionEstadoInvalidaException.class)
                .hasMessage(mensaje);
    }
}