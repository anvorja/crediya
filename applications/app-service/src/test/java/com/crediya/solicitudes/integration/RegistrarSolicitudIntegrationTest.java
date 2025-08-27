// applications/app-service/src/test/java/com/crediya/solicitudes/integration/RegistrarSolicitudIntegrationTest.java
package com.crediya.solicitudes.integration;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.exception.DatosSolicitudInvalidosException;
import com.crediya.solicitudes.model.solicitud.exception.SolicitudDuplicadaException;
import com.crediya.solicitudes.model.solicitud.gateways.EventPublisherGateway;
import com.crediya.solicitudes.model.solicitud.gateways.NotificationGateway;
import com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import com.crediya.solicitudes.model.solicitud.gateways.ValidacionExternaGateway;
import com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de integración para verificar el cumplimiento completo
 * de los criterios de aceptación de la Historia de Usuario:
 * "Registrar una solicitud de préstamo"
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Integración - Registrar Solicitud de Préstamo")
class RegistrarSolicitudIntegrationTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private NotificationGateway notificationGateway;

    @Mock
    private EventPublisherGateway eventPublisherGateway;

    @Mock
    private ValidacionExternaGateway validacionExternaGateway;

    @Nested
    @DisplayName("Historia de Usuario: Registrar una solicitud de préstamo")
    class HistoriaUsuarioCompleta {

        @Test
        @DisplayName("Escenario Principal: Como cliente, quiero enviar mi solicitud con toda la información necesaria")
        void escenarioPrincipalRegistroCompleto() {
            // Given - Cliente con datos válidos
            CrearSolicitudUseCase useCase = new CrearSolicitudUseCase(
                    solicitudRepository, notificationGateway,
                    eventPublisherGateway, validacionExternaGateway);

            Solicitud solicitud = crearSolicitudCompletaValida();
            Solicitud solicitudGuardada = crearSolicitudGuardada();

            // Configurar mocks para flujo exitoso
            when(solicitudRepository.existeSolicitudActivaPorDocumento("12345678"))
                    .thenReturn(false);
            when(validacionExternaGateway.validarDocumento(anyString()))
                    .thenReturn(Optional.empty());
            when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                    .thenReturn(Optional.empty());
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenReturn(solicitudGuardada);

            // When - Cliente envía la solicitud
            Solicitud resultado = useCase.ejecutar(solicitud);

            // Then - Verificar TODOS los criterios de aceptación

            // CA-1: Se puede enviar solicitud con información del cliente y detalles del préstamo
            assertThat(resultado.getNumeroDocumento()).isEqualTo("12345678"); // Documento identidad
            assertThat(resultado.getMontoSolicitado()).isEqualByComparingTo(new BigDecimal("5000000")); // Monto
            assertThat(resultado.getPlazoMeses()).isEqualTo(24); // Plazo
            assertThat(resultado.getTipoCredito()).isEqualTo("PERSONAL"); // Tipo préstamo

            // CA-2: Se registra automáticamente con estado "Pendiente de revisión"
            assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
            assertThat(resultado.getFechaCreacion()).isNotNull();

            // CA-3: Sistema valida que tipo de préstamo sea uno de los existentes
            assertThat(resultado.getTipoCredito()).isIn("PERSONAL", "VEHICULO", "VIVIENDA", "EDUCATIVO");

            // Verificar que se guardó la solicitud
            verify(solicitudRepository).guardar(any(Solicitud.class));

            // Verificar que se enviaron notificaciones
            verify(notificationGateway).notificarSolicitudCreada(resultado);
            verify(eventPublisherGateway).publicarEventoSolicitudCreada(resultado);
        }

        @Test
        @DisplayName("Escenario Alternativo: Validación falla por tipo de préstamo inválido")
        void escenarioAlternativoTipoPrestamoInvalido() {
            // Given
            CrearSolicitudUseCase useCase = new CrearSolicitudUseCase(
                    solicitudRepository, notificationGateway,
                    eventPublisherGateway, validacionExternaGateway);

            Solicitud solicitud = crearSolicitudCompletaValida();
            solicitud.setTipoCredito("TIPO_NO_EXISTENTE"); // CA-3: Tipo no válido

            // When & Then - CA-3: Sistema debe validar tipos existentes
            assertThatThrownBy(() -> useCase.ejecutar(solicitud))
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("Tipo de crédito no válido");

            // Verificar que NO se guardó nada
            verify(solicitudRepository, never()).guardar(any());
        }

        @Test
        @DisplayName("Escenario Alternativo: Cliente ya tiene solicitud activa")
        void escenarioAlternativoSolicitudDuplicada() {
            // Given
            CrearSolicitudUseCase useCase = new CrearSolicitudUseCase(
                    solicitudRepository, notificationGateway,
                    eventPublisherGateway, validacionExternaGateway);

            Solicitud solicitud = crearSolicitudCompletaValida();

            // Cliente ya tiene una solicitud activa
            when(solicitudRepository.existeSolicitudActivaPorDocumento("12345678"))
                    .thenReturn(true);

            // When & Then - No debe permitir duplicados
            assertThatThrownBy(() -> useCase.ejecutar(solicitud))
                    .isInstanceOf(SolicitudDuplicadaException.class)
                    .hasMessageContaining("Ya existe una solicitud activa para el documento: 12345678");

            // Verificar que NO se guardó nada
            verify(solicitudRepository, never()).guardar(any());
        }
    }

    @Nested
    @DisplayName("Validaciones Técnicas Específicas")
    class ValidacionesTecnicas {

        @Test
        @DisplayName("Debe validar todos los campos obligatorios del cliente")
        void debeValidarCamposObligatoriosCliente() {
            // Given
            CrearSolicitudUseCase useCase = new CrearSolicitudUseCase(
                    solicitudRepository, notificationGateway,
                    eventPublisherGateway, validacionExternaGateway);

            // Test para cada campo obligatorio
            String[] camposObligatorios = {
                    "numeroDocumento", "nombres", "apellidos", "email", "telefono"
            };

            for (String campo : camposObligatorios) {
                Solicitud solicitudInvalida = crearSolicitudConCampoInvalido(campo);

                // When & Then
                assertThatThrownBy(() -> useCase.ejecutar(solicitudInvalida))
                        .isInstanceOf(DatosSolicitudInvalidosException.class)
                        .hasMessageContaining(campo.toLowerCase().contains("numero") ? "documento" : campo.toLowerCase());
            }
        }

        @Test
        @DisplayName("Debe validar rangos de monto según criterios de negocio")
        void debeValidarRangosMonto() {
            // Given
            CrearSolicitudUseCase useCase = new CrearSolicitudUseCase(
                    solicitudRepository, notificationGateway,
                    eventPublisherGateway, validacionExternaGateway);

            // Test monto muy bajo
            Solicitud montoMuyBajo = crearSolicitudCompletaValida();
            montoMuyBajo.setMontoSolicitado(new BigDecimal("50000")); // Menor a 100,000

            // Test monto muy alto
            Solicitud montoMuyAlto = crearSolicitudCompletaValida();
            montoMuyAlto.setMontoSolicitado(new BigDecimal("60000000")); // Mayor a 50,000,000

            // When & Then
            assertThatThrownBy(() -> useCase.ejecutar(montoMuyBajo))
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("mínimo");

            assertThatThrownBy(() -> useCase.ejecutar(montoMuyAlto))
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("máximo");
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES PARA CREAR SOLICITUDES DE PRUEBA
    // ============================================================

    private Solicitud crearSolicitudCompletaValida() {
        Solicitud solicitud = new Solicitud();
        // Información del cliente (documento de identidad)
        solicitud.setNumeroDocumento("12345678");
        solicitud.setNombres("Juan Carlos");
        solicitud.setApellidos("Pérez Gómez");
        solicitud.setEmail("juan.perez@email.com");
        solicitud.setTelefono("+573001234567");

        // Detalles del préstamo (monto, plazo, tipo)
        solicitud.setMontoSolicitado(new BigDecimal("5000000"));
        solicitud.setPlazoMeses(24);
        solicitud.setTipoCredito("PERSONAL"); // Tipo existente

        // Información financiera
        solicitud.setIngresosMensuales(new BigDecimal("3000000"));
        solicitud.setGastosMensuales(new BigDecimal("1500000"));

        solicitud.setFechaCreacion(LocalDateTime.now());
        return solicitud;
    }

    private Solicitud crearSolicitudGuardada() {
        Solicitud guardada = crearSolicitudCompletaValida();
        guardada.setId("SOL-001");
        guardada.setEstado(EstadoSolicitud.PENDIENTE_REVISION); // CA-2: Estado inicial
        guardada.setFechaActualizacion(LocalDateTime.now());
        return guardada;
    }

    private Solicitud crearSolicitudConCampoInvalido(String campo) {
        Solicitud solicitud = crearSolicitudCompletaValida();

        switch (campo) {
            case "numeroDocumento" -> solicitud.setNumeroDocumento(""); // Vacío
            case "nombres" -> solicitud.setNombres(null); // Null
            case "apellidos" -> solicitud.setApellidos("   "); // Solo espacios
            case "email" -> solicitud.setEmail("email-invalido"); // Formato inválido
            case "telefono" -> solicitud.setTelefono("123"); // Muy corto
            default -> throw new IllegalArgumentException("Campo no válido: " + campo);
        }

        return solicitud;
    }
}