// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/SolicitudTest.java
package com.crediya.solicitudes.model.solicitud;

import com.crediya.solicitudes.model.solicitud.exception.DatosSolicitudInvalidosException;
import com.crediya.solicitudes.model.solicitud.exception.TransicionEstadoInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class SolicitudTest {

    @Nested
    @DisplayName("Validación de Datos")
    class ValidacionDatos {

        @Test
        @DisplayName("Debe validar solicitud con datos correctos")
        void debeValidarSolicitudConDatosCorrectos() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // When & Then - TEST SIMPLE SIN REACTOR
            assertThatNoException().isThrownBy(solicitud::validarDatos);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "12345", "1234567890123456"})
        @DisplayName("Debe fallar con documento inválido")
        void debeFallarConDocumentoInvalido(String documento) {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setNumeroDocumento(documento);

            // When & Then
            assertThatThrownBy(solicitud::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("documento");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        @DisplayName("Debe fallar con nombres vacíos")
        void debeFallarConNombresVacios(String nombres) {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setNombres(nombres);

            // When & Then
            assertThatThrownBy(solicitud::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("nombres");
        }

        @ParameterizedTest
        @ValueSource(strings = {"email-invalido", "test@", "@domain.com", ""})
        @DisplayName("Debe fallar con email inválido")
        void debeFallarConEmailInvalido(String email) {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setEmail(email);

            // When & Then
            assertThatThrownBy(solicitud::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("Debe fallar con monto menor al mínimo")
        void debeFallarConMontoMenorAlMinimo() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setMontoSolicitado(new BigDecimal("50000"));

            // When & Then
            assertThatThrownBy(solicitud::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("mínimo");
        }

        @Test
        @DisplayName("Debe fallar con monto mayor al máximo")
        void debeFallarConMontoMayorAlMaximo() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setMontoSolicitado(new BigDecimal("51000000"));

            // When & Then
            assertThatThrownBy(solicitud::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("máximo");
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALIDO", "OTRO", ""})
        @DisplayName("Debe fallar con tipo de crédito inválido")
        void debeFallarConTipoCreditoInvalido(String tipoCredito) {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setTipoCredito(tipoCredito);

            // When & Then
            assertThatThrownBy(solicitud::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("Tipo de crédito no válido");
        }
    }

    @Nested
    @DisplayName("Evaluación de Capacidad de Pago")
    class EvaluacionCapacidadPago {

        @Test
        @DisplayName("Debe aprobar con buena capacidad de pago")
        void debeAprobarConBuenaCapacidadPago() {
            // Given
            Solicitud solicitud = crearSolicitudConCapacidadPago(
                    new BigDecimal("5000000"), // ingresos
                    new BigDecimal("3000000"), // gastos (60%)
                    new BigDecimal("10000000") // monto (2x ingresos)
            );

            // When - TEST SIMPLE SIN REACTOR
            boolean resultado = solicitud.evaluarCapacidadPago();

            // Then
            assertThat(resultado).isTrue();
            assertThat(solicitud.getObservaciones()).doesNotContain("superan");
        }

        @Test
        @DisplayName("Debe rechazar con gastos excesivos")
        void debeRechazarConGastosExcesivos() {
            // Given
            Solicitud solicitud = crearSolicitudConCapacidadPago(
                    new BigDecimal("5000000"), // ingresos
                    new BigDecimal("3600000"), // gastos (72%)
                    new BigDecimal("10000000") // monto
            );

            // When
            boolean resultado = solicitud.evaluarCapacidadPago();

            // Then
            assertThat(resultado).isFalse();
            assertThat(solicitud.getObservaciones()).contains("70% de los ingresos");
        }

        @Test
        @DisplayName("Debe rechazar con monto excesivo")
        void debeRechazarConMontoExcesivo() {
            // Given
            Solicitud solicitud = crearSolicitudConCapacidadPago(
                    new BigDecimal("5000000"), // ingresos
                    new BigDecimal("2000000"), // gastos (40%)
                    new BigDecimal("30000000") // monto (6x ingresos)
            );

            // When
            boolean resultado = solicitud.evaluarCapacidadPago();

            // Then
            assertThat(resultado).isFalse();
            assertThat(solicitud.getObservaciones()).contains("5 veces los ingresos");
        }

        @Test
        @DisplayName("Debe rechazar sin información financiera")
        void debeRechazarSinInformacionFinanciera() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setIngresosMensuales(null);
            solicitud.setGastosMensuales(null);

            // When
            boolean resultado = solicitud.evaluarCapacidadPago();

            // Then
            assertThat(resultado).isFalse();
        }
    }

    @Nested
    @DisplayName("Pre-aprobación Automática")
    class PreAprobacionAutomatica {

        @Test
        @DisplayName("Debe pre-aprobar automáticamente con buena capacidad")
        void debePreAprobarAutomaticamente() {
            // Given
            Solicitud solicitud = crearSolicitudConCapacidadPago(
                    new BigDecimal("5000000"),
                    new BigDecimal("2000000"),
                    new BigDecimal("10000000")
            );

            // When - TEST SIMPLE SIN REACTOR
            solicitud.preAprobarAutomaticamente();

            // Then
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PRE_APROBADA);
            assertThat(solicitud.getObservaciones()).contains("Pre-aprobada automáticamente");
        }

        @Test
        @DisplayName("Debe mantener en revisión con mala capacidad")
        void debeMantenerEnRevisionConMalaCapacidad() {
            // Given
            Solicitud solicitud = crearSolicitudConCapacidadPago(
                    new BigDecimal("5000000"),
                    new BigDecimal("4000000"), // 80% gastos
                    new BigDecimal("10000000")
            );

            // When
            solicitud.preAprobarAutomaticamente();

            // Then
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
            assertThat(solicitud.getObservaciones()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Transiciones de Estado")
    class TransicionesEstado {

        @Test
        @DisplayName("Debe permitir transición de PENDIENTE_REVISION a PRE_APROBADA")
        void debePermitirTransicionPendienteAPreAprobada() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // When
            solicitud.cambiarEstado(EstadoSolicitud.PRE_APROBADA, "Pre-aprobada automáticamente");

            // Then
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PRE_APROBADA);
            assertThat(solicitud.getObservaciones()).isEqualTo("Pre-aprobada automáticamente");
            assertThat(solicitud.getFechaActualizacion()).isNotNull();
        }

        @Test
        @DisplayName("No debe permitir transición de APROBADA a PENDIENTE_REVISION")
        void noDebePermitirTransicionAprobadaAPendiente() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setEstado(EstadoSolicitud.APROBADA);

            // When & Then
            assertThatThrownBy(() ->
                    solicitud.cambiarEstado(EstadoSolicitud.PENDIENTE_REVISION, "Intento inválido"))
                    .isInstanceOf(TransicionEstadoInvalidaException.class)
                    .hasMessageContaining("No se puede cambiar de APROBADA a PENDIENTE_REVISION");
        }

        @Test
        @DisplayName("No debe permitir cambios desde RECHAZADA")
        void noDebePermitirCambiosDesdeRechazada() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);

            // When & Then
            assertThatThrownBy(() ->
                    solicitud.cambiarEstado(EstadoSolicitud.APROBADA, "Intento inválido"))
                    .isInstanceOf(TransicionEstadoInvalidaException.class);
        }
    }

    @Nested
    @DisplayName("Métodos Auxiliares")
    class MetodosAuxiliares {

        @Test
        @DisplayName("Debe calcular capacidad de endeudamiento correctamente")
        void debeCalcularCapacidadEndeudamientoCorrectamente() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setIngresosMensuales(new BigDecimal("5000000")); // 5M
            solicitud.setGastosMensuales(new BigDecimal("3000000"));   // 3M

            // When
            BigDecimal capacidad = solicitud.calcularCapacidadEndeudamiento();

            // Then
            // Ingreso disponible: 5M - 3M = 2M
            // Capacidad: 2M * 0.30 = 600,000
            assertThat(capacidad).isEqualByComparingTo(new BigDecimal("600000"));
        }

        @Test
        @DisplayName("Debe retornar nombre completo")
        void debeRetornarNombreCompleto() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // When
            String nombreCompleto = solicitud.getNombreCompleto();

            // Then
            assertThat(nombreCompleto).isEqualTo("Juan Carlos Pérez Gómez");
        }

        @Test
        @DisplayName("Debe identificar si puede ser editada")
        void debeIdentificarSiPuedeSerEditada() {
            // Given
            Solicitud pendiente = crearSolicitudValida();
            pendiente.setEstado(EstadoSolicitud.PENDIENTE_REVISION);

            Solicitud aprobada = crearSolicitudValida();
            aprobada.setEstado(EstadoSolicitud.APROBADA);

            // When & Then
            assertThat(pendiente.puedeSerEditada()).isTrue();
            assertThat(aprobada.puedeSerEditada()).isFalse();
        }

        @Test
        @DisplayName("Debe identificar estados finales")
        void debeIdentificarEstadosFinales() {
            // Given
            Solicitud aprobada = crearSolicitudValida();
            aprobada.setEstado(EstadoSolicitud.APROBADA);

            Solicitud rechazada = crearSolicitudValida();
            rechazada.setEstado(EstadoSolicitud.RECHAZADA);

            Solicitud pendiente = crearSolicitudValida();

            // When & Then
            assertThat(aprobada.estaEnEstadoFinal()).isTrue();
            assertThat(rechazada.estaEnEstadoFinal()).isTrue();
            assertThat(pendiente.estaEnEstadoFinal()).isFalse();
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES PARA CREAR SOLICITUDES DE PRUEBA (SIN LOMBOK)
    // ============================================================

    private Solicitud crearSolicitudValida() {
        Solicitud solicitud = new Solicitud();
        solicitud.setNumeroDocumento("12345678");
        solicitud.setNombres("Juan Carlos");
        solicitud.setApellidos("Pérez Gómez");
        solicitud.setEmail("juan.perez@email.com");
        solicitud.setTelefono("+573001234567");
        solicitud.setMontoSolicitado(new BigDecimal("5000000"));
        solicitud.setTipoCredito("PERSONAL");
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        solicitud.setFechaCreacion(LocalDateTime.now());
        return solicitud;
    }

    private Solicitud crearSolicitudConCapacidadPago(BigDecimal ingresos, BigDecimal gastos, BigDecimal monto) {
        Solicitud solicitud = crearSolicitudValida();
        solicitud.setIngresosMensuales(ingresos);
        solicitud.setGastosMensuales(gastos);
        solicitud.setMontoSolicitado(monto);
        return solicitud;
    }
}