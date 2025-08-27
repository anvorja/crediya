// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/EstadoSolicitudTest.java
package com.crediya.solicitudes.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class EstadoSolicitudTest {

    @Test
    @DisplayName("Debe tener descripción para todos los estados")
    void debeTenerDescripcionParaTodosLosEstados() {
        // When & Then
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            assertThat(estado.getDescripcion()).isNotBlank();
        }
    }

    @ParameterizedTest
    @MethodSource("transicionesValidasProvider")
    @DisplayName("Debe permitir transiciones válidas")
    void debePermitirTransicionesValidas(EstadoSolicitud origen, EstadoSolicitud destino) {
        // When
        boolean puedeTransicionar = origen.puedeTransicionarA(destino);

        // Then
        assertThat(puedeTransicionar).isTrue();
    }

    @ParameterizedTest
    @MethodSource("transicionesInvalidasProvider")
    @DisplayName("Debe rechazar transiciones inválidas")
    void debeRechazarTransicionesInvalidas(EstadoSolicitud origen, EstadoSolicitud destino) {
        // When
        boolean puedeTransicionar = origen.puedeTransicionarA(destino);

        // Then
        assertThat(puedeTransicionar).isFalse();
    }

    static Stream<Arguments> transicionesValidasProvider() {
        return Stream.of(
                Arguments.of(EstadoSolicitud.PENDIENTE_REVISION, EstadoSolicitud.PRE_APROBADA),
                Arguments.of(EstadoSolicitud.PENDIENTE_REVISION, EstadoSolicitud.APROBADA),
                Arguments.of(EstadoSolicitud.PENDIENTE_REVISION, EstadoSolicitud.RECHAZADA),
                Arguments.of(EstadoSolicitud.PRE_APROBADA, EstadoSolicitud.APROBADA),
                Arguments.of(EstadoSolicitud.PRE_APROBADA, EstadoSolicitud.RECHAZADA)
        );
    }

    static Stream<Arguments> transicionesInvalidasProvider() {
        return Stream.of(
                Arguments.of(EstadoSolicitud.APROBADA, EstadoSolicitud.PENDIENTE_REVISION),
                Arguments.of(EstadoSolicitud.APROBADA, EstadoSolicitud.PRE_APROBADA),
                Arguments.of(EstadoSolicitud.APROBADA, EstadoSolicitud.RECHAZADA),
                Arguments.of(EstadoSolicitud.RECHAZADA, EstadoSolicitud.PENDIENTE_REVISION),
                Arguments.of(EstadoSolicitud.RECHAZADA, EstadoSolicitud.PRE_APROBADA),
                Arguments.of(EstadoSolicitud.RECHAZADA, EstadoSolicitud.APROBADA)
        );
    }
}