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

@DisplayName("Solicitud - Entidad del Dominio")
class SolicitudTest {

    @Nested
    @DisplayName("Criterios de Aceptación - Validación de Datos")
    class CriteriosAceptacionValidacion {

        @Test
        @DisplayName("CA-1: Debe validar solicitud con datos completos del cliente")
        void debeValidarSolicitudConDatosCompletosCliente() {
            // Given - Solicitud con datos completos según criterios de aceptación
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setNumeroDocumento("12345678");    // Documento de identidad
            solicitud.setNombres("Juan Carlos");
            solicitud.setApellidos("Pérez Gómez");
            solicitud.setEmail("juan.perez@email.com");
            solicitud.setTelefono("+573001234567");

            // When & Then - No debe lanzar excepción
            assertThatNoException().isThrownBy(solicitud::validarDatos);
        }

        @Test
        @DisplayName("CA-1: Debe validar solicitud con detalles del préstamo")
        void debeValidarSolicitudConDetallesPrestamo() {
            // Given - Solicitud con detalles del préstamo según criterios
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setMontoSolicitado(new BigDecimal("5000000"));  // Monto
            solicitud.setPlazoMeses(24);                              // Plazo
            solicitud.setTipoCredito("PERSONAL");                     // Tipo de préstamo

            // When & Then - No debe lanzar excepción
            assertThatNoException().isThrownBy(solicitud::validarDatos);
        }

        @Test
        @DisplayName("CA-2: Debe asignar estado inicial PENDIENTE_REVISION")
        void debeAsignarEstadoInicialPendienteRevision() {
            // Given
            Solicitud solicitud = new Solicitud();

            // When - Asignar estado inicial
            solicitud.asignarEstadoInicial();

            // Then
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
            assertThat(solicitud.getFechaCreacion()).isNotNull();
        }

        @Test
        @DisplayName("CA-3: Debe validar tipos de préstamo permitidos")
        void debeValidarTiposPrestamoPermitidos() {
            // Given - Tipos válidos según BusinessConstants
            String[] tiposValidos = {"PERSONAL", "VEHICULO", "VIVIENDA", "EDUCATIVO"};

            for (String tipo : tiposValidos) {
                Solicitud solicitud = crearSolicitudValida();
                solicitud.setTipoCredito(tipo);

                // When & Then - No debe lanzar excepción
                assertThatNoException().isThrownBy(solicitud::validarDatos);
            }
        }

        @Test
        @DisplayName("CA-3: Debe rechazar tipos de préstamo no válidos")
        void debeRechazarTiposPrestamoNoValidos() {
            // Given - Tipos inválidos
            String[] tiposInvalidos = {"HIPOTECARIO", "COMERCIAL", "TIPO_INEXISTENTE"};

            for (String tipo : tiposInvalidos) {
                Solicitud solicitud = crearSolicitudValida();
                solicitud.setTipoCredito(tipo);

                // When & Then
                assertThatThrownBy(solicitud::validarDatos)
                        .isInstanceOf(DatosSolicitudInvalidosException.class)
                        .hasMessageContaining("Tipo de crédito no válido");
            }
        }
    }

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
            solicitud.setMontoSolicitado(new BigDecimal("99999")); // Menor a 100,000

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
            solicitud.setMontoSolicitado(new BigDecimal("50000001")); // Mayor a 50,000,000

            // When & Then
            assertThatThrownBy(solicitud::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("máximo");
        }

        @ParameterizedTest
        @ValueSource(strings = {"123456789", "+5730012345678901", "abcd1234"})
        @DisplayName("Debe fallar con teléfono inválido")
        void debeFallarConTelefonoInvalido(String telefono) {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setTelefono(telefono);

            // When & Then
            assertThatThrownBy(solicitud::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("teléfono");
        }
    }

    @Nested
    @DisplayName("Gestión de Estados")
    class GestionEstados {

        @Test
        @DisplayName("Debe permitir transición válida de PENDIENTE a EN_REVISION")
        void debePermitirTransicionValidaPendienteAEnRevision() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);

            // When
            solicitud.cambiarEstado(EstadoSolicitud.EN_REVISION, "Iniciando revisión");

            // Then
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.EN_REVISION);
            assertThat(solicitud.getObservaciones()).contains("Iniciando revisión");
            assertThat(solicitud.getFechaActualizacion()).isNotNull();
        }

        @Test
        @DisplayName("Debe permitir transición válida de EN_REVISION a APROBADA")
        void debePermitirTransicionValidaEnRevisionAAprobada() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setEstado(EstadoSolicitud.EN_REVISION);

            // When
            solicitud.cambiarEstado(EstadoSolicitud.APROBADA, "Solicitud aprobada");

            // Then
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.APROBADA);
            assertThat(solicitud.getFechaActualizacion()).isNotNull();
        }

        @Test
        @DisplayName("No debe permitir transición inválida de APROBADA a PENDIENTE")
        void noDebePermitirTransicionInvalidaAprobadaAPendiente() {
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

        @Test
        @DisplayName("Debe validar capacidad de pago correctamente")
        void debeValidarCapacidadPagoCorrectamente() {
            // Given - Cliente con capacidad suficiente
            Solicitud solicitudViable = crearSolicitudConCapacidadPago(
                    new BigDecimal("5000000"), // Ingresos
                    new BigDecimal("2000000"), // Gastos
                    new BigDecimal("500000")   // Monto solicitado
            );

            // Given - Cliente sin capacidad suficiente
            Solicitud solicitudNoViable = crearSolicitudConCapacidadPago(
                    new BigDecimal("3000000"), // Ingresos
                    new BigDecimal("2800000"), // Gastos
                    new BigDecimal("500000")   // Monto solicitado (excede capacidad)
            );

            // When & Then
            assertThat(solicitudViable.tieneCapacidadPago()).isTrue();
            assertThat(solicitudNoViable.tieneCapacidadPago()).isFalse();
        }

        @Test
        @DisplayName("Debe generar resumen de solicitud")
        void debeGenerarResumenSolicitud() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setId("SOL-001");

            // When
            String resumen = solicitud.generarResumen();

            // Then
            assertThat(resumen)
                    .contains("SOL-001")
                    .contains("Juan Carlos Pérez Gómez")
                    .contains("PERSONAL")
                    .contains("5,000,000")
                    .contains("PENDIENTE_REVISION");
        }
    }

    @Nested
    @DisplayName("Validaciones de Negocio Específicas")
    class ValidacionesNegocioEspecificas {

        @Test
        @DisplayName("Debe validar plazo según tipo de crédito")
        void debeValidarPlazoSegunTipoCredito() {
            // Given - Crédito personal con plazo válido
            Solicitud personal = crearSolicitudValida();
            personal.setTipoCredito("PERSONAL");
            personal.setPlazoMeses(36); // Válido para personal (6-60 meses)

            // Given - Crédito de vivienda con plazo válido
            Solicitud vivienda = crearSolicitudValida();
            vivienda.setTipoCredito("VIVIENDA");
            vivienda.setPlazoMeses(240); // Válido para vivienda (hasta 360 meses)

            // When & Then - Ambos deben ser válidos
            assertThatNoException().isThrownBy(personal::validarDatos);
            assertThatNoException().isThrownBy(vivienda::validarDatos);
        }

        @Test
        @DisplayName("Debe rechazar plazo inválido para tipo de crédito")
        void debeRechazarPlazoInvalidoParaTipoCredito() {
            // Given - Crédito personal con plazo muy largo
            Solicitud personal = crearSolicitudValida();
            personal.setTipoCredito("PERSONAL");
            personal.setPlazoMeses(121); // Excede máximo para personal (120 meses)

            // When & Then
            assertThatThrownBy(personal::validarDatos)
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("plazo");
        }

        @Test
        @DisplayName("Debe validar coherencia entre monto y tipo de crédito")
        void debeValidarCoherenciaMontoTipoCredito() {
            // Given - Crédito educativo con monto razonable
            Solicitud educativo = crearSolicitudValida();
            educativo.setTipoCredito("EDUCATIVO");
            educativo.setMontoSolicitado(new BigDecimal("10000000")); // 10M para educación

            // When & Then
            assertThatNoException().isThrownBy(educativo::validarDatos);
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
        solicitud.setPlazoMeses(24);
        solicitud.setTipoCredito("PERSONAL");
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        solicitud.setFechaCreacion(LocalDateTime.now());
        solicitud.setIngresosMensuales(new BigDecimal("3000000"));
        solicitud.setGastosMensuales(new BigDecimal("1500000"));
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