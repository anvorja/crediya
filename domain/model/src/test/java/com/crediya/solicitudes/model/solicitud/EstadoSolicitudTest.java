// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/EstadoSolicitudTest.java
package com.crediya.solicitudes.model.solicitud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("EstadoSolicitud - Enum de Estados")
class EstadoSolicitudTest {

    @Test
    @DisplayName("Debe tener todos los estados esperados")
    void debeTenerTodosLosEstadosEsperados() {
        // Given
        EstadoSolicitud[] estados = EstadoSolicitud.values();

        // Then
        assertThat(estados).hasSize(4);
        assertThat(Arrays.asList(estados)).containsExactlyInAnyOrder(
                EstadoSolicitud.PENDIENTE_REVISION,
                EstadoSolicitud.EN_REVISION,
                EstadoSolicitud.APROBADA,
                EstadoSolicitud.RECHAZADA
        );
    }

    @Test
    @DisplayName("Debe identificar estados finales correctamente")
    void debeIdentificarEstadosFinalesCorrectamente() {
        // Then
        assertThat(EstadoSolicitud.APROBADA.esFinal()).isTrue();
        assertThat(EstadoSolicitud.RECHAZADA.esFinal()).isTrue();
        assertThat(EstadoSolicitud.PENDIENTE_REVISION.esFinal()).isFalse();
        assertThat(EstadoSolicitud.EN_REVISION.esFinal()).isFalse();
    }

    @Test
    @DisplayName("Debe identificar estados editables correctamente")
    void debeIdentificarEstadosEditablesCorrectamente() {
        // Then
        assertThat(EstadoSolicitud.PENDIENTE_REVISION.esEditable()).isTrue();
        assertThat(EstadoSolicitud.EN_REVISION.esEditable()).isFalse();
        assertThat(EstadoSolicitud.APROBADA.esEditable()).isFalse();
        assertThat(EstadoSolicitud.RECHAZADA.esEditable()).isFalse();
    }

    @Test
    @DisplayName("Debe validar transiciones desde PENDIENTE_REVISION")
    void debeValidarTransicionesDesdePendienteRevision() {
        // Given
        EstadoSolicitud estadoActual = EstadoSolicitud.PENDIENTE_REVISION;

        // Then - Transiciones válidas
        assertThat(estadoActual.puedeTransicionarA(EstadoSolicitud.EN_REVISION)).isTrue();
        assertThat(estadoActual.puedeTransicionarA(EstadoSolicitud.APROBADA)).isTrue();
        assertThat(estadoActual.puedeTransicionarA(EstadoSolicitud.RECHAZADA)).isTrue();

        // Transición inválida (mismo estado)
        assertThat(estadoActual.puedeTransicionarA(EstadoSolicitud.PENDIENTE_REVISION)).isFalse();
    }

    @Test
    @DisplayName("Debe validar transiciones desde EN_REVISION")
    void debeValidarTransicionesDesdeEnRevision() {
        // Given
        EstadoSolicitud estadoActual = EstadoSolicitud.EN_REVISION;

        // Then - Transiciones válidas
        assertThat(estadoActual.puedeTransicionarA(EstadoSolicitud.APROBADA)).isTrue();
        assertThat(estadoActual.puedeTransicionarA(EstadoSolicitud.RECHAZADA)).isTrue();

        // Transiciones inválidas
        assertThat(estadoActual.puedeTransicionarA(EstadoSolicitud.PENDIENTE_REVISION)).isFalse();
        assertThat(estadoActual.puedeTransicionarA(EstadoSolicitud.EN_REVISION)).isFalse();
    }

    @Test
    @DisplayName("No debe permitir transiciones desde estados finales")
    void noDebePermitirTransicionesDesdeEstadosFinales() {
        // Given
        EstadoSolicitud aprobada = EstadoSolicitud.APROBADA;
        EstadoSolicitud rechazada = EstadoSolicitud.RECHAZADA;

        // Then - Ninguna transición debe ser válida desde estados finales
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            assertThat(aprobada.puedeTransicionarA(estado)).isFalse();
            assertThat(rechazada.puedeTransicionarA(estado)).isFalse();
        }
    }

    @ParameterizedTest
    @EnumSource(EstadoSolicitud.class)
    @DisplayName("Debe obtener estados siguientes válidos")
    void debeObtenerEstadosSiguientesValidos(EstadoSolicitud estado) {
        // When
        Set<EstadoSolicitud> siguientes = estado.obtenerEstadosSiguientesValidos();

        // Then
        switch (estado) {
            case PENDIENTE_REVISION ->
                    assertThat(siguientes).containsExactlyInAnyOrder(
                    EstadoSolicitud.EN_REVISION,
                    EstadoSolicitud.APROBADA,
                    EstadoSolicitud.RECHAZADA
            );
            case EN_REVISION ->
                    assertThat(siguientes).containsExactlyInAnyOrder(
                    EstadoSolicitud.APROBADA,
                    EstadoSolicitud.RECHAZADA
            );
            case APROBADA, RECHAZADA -> assertThat(siguientes).isEmpty();
        }
    }

    @Test
    @DisplayName("Debe obtener descripción legible del estado")
    void debeObtenerDescripcionLegibleDelEstado() {
        // Then
        assertThat(EstadoSolicitud.PENDIENTE_REVISION.getDescripcion())
                .isEqualTo("Pendiente de Revisión");
        assertThat(EstadoSolicitud.EN_REVISION.getDescripcion())
                .isEqualTo("En Revisión");
        assertThat(EstadoSolicitud.APROBADA.getDescripcion())
                .isEqualTo("Aprobada");
        assertThat(EstadoSolicitud.RECHAZADA.getDescripcion())
                .isEqualTo("Rechazada");
    }

    @Test
    @DisplayName("Debe obtener estado inicial por defecto")
    void debeObtenerEstadoInicialPorDefecto() {
        // When
        EstadoSolicitud inicial = EstadoSolicitud.getEstadoInicial();

        // Then
        assertThat(inicial).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
    }

    @Test
    @DisplayName("Debe crear estado desde string")
    void debeCrearEstadoDesdeString() {
        // When & Then
        assertThat(EstadoSolicitud.fromString("PENDIENTE_REVISION"))
                .isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
        assertThat(EstadoSolicitud.fromString("EN_REVISION"))
                .isEqualTo(EstadoSolicitud.EN_REVISION);
        assertThat(EstadoSolicitud.fromString("APROBADA"))
                .isEqualTo(EstadoSolicitud.APROBADA);
        assertThat(EstadoSolicitud.fromString("RECHAZADA"))
                .isEqualTo(EstadoSolicitud.RECHAZADA);
    }

    @Test
    @DisplayName("Debe fallar al crear estado desde string inválido")
    void debeFallarAlCrearEstadoDesdeStringInvalido() {
        // When & Then
        assertThatThrownBy(() -> EstadoSolicitud.fromString("ESTADO_INEXISTENTE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado no válido: ESTADO_INEXISTENTE");

        assertThatThrownBy(() -> EstadoSolicitud.fromString(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> EstadoSolicitud.fromString(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Debe obtener todos los estados no finales")
    void debeObtenerTodosLosEstadosNoFinales() {
        // When
        Set<EstadoSolicitud> noFinales = EstadoSolicitud.getEstadosNoFinales();

        // Then
        assertThat(noFinales).containsExactlyInAnyOrder(
                EstadoSolicitud.PENDIENTE_REVISION,
                EstadoSolicitud.EN_REVISION
        );
    }

    @Test
    @DisplayName("Debe obtener todos los estados finales")
    void debeObtenerTodosLosEstadosFinales() {
        // When
        Set<EstadoSolicitud> finales = EstadoSolicitud.getEstadosFinales();

        // Then
        assertThat(finales).containsExactlyInAnyOrder(
                EstadoSolicitud.APROBADA,
                EstadoSolicitud.RECHAZADA
        );
    }
}