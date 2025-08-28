// domain/usecase/src/test/java/com/crediya/solicitudes/usecase/crearsolicitud/CrearSolicitudUseCaseTest.java
package com.crediya.solicitudes.usecase.crearsolicitud;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.exception.DatosSolicitudInvalidosException;
import com.crediya.solicitudes.model.solicitud.exception.SolicitudDuplicadaException;
import com.crediya.solicitudes.model.solicitud.gateways.EventPublisherGateway;
import com.crediya.solicitudes.model.solicitud.gateways.NotificationGateway;
import com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import com.crediya.solicitudes.model.solicitud.gateways.ValidacionExternaGateway;
import com.crediya.solicitudes.model.solicitud.valueobjects.HistorialCrediticio;
import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionDocumento;
import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionFinanciera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Integración - Crear Solicitud UseCase con Validaciones Mejoradas")
class CrearSolicitudIntegrationTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private NotificationGateway notificationGateway;
    @Mock
    private EventPublisherGateway eventPublisherGateway;
    @Mock
    private ValidacionExternaGateway validacionExternaGateway;

    private CrearSolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CrearSolicitudUseCase(
                solicitudRepository, notificationGateway,
                eventPublisherGateway, validacionExternaGateway
        );

        // Setup mocks por defecto
        when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                .thenReturn(Mono.just(false));

        when(validacionExternaGateway.validarDocumento(anyString()))
                .thenReturn(Mono.just(new ValidacionDocumento(true, "Juan", "Pérez")));

        when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                .thenReturn(Mono.just(new HistorialCrediticio(false, 650)));

        when(validacionExternaGateway.validarInformacionFinanciera(anyString(), any()))
                .thenReturn(Mono.just(new ValidacionFinanciera(
                        true, new BigDecimal("3000000"), "MOCK", "Validación exitosa")));

        when(solicitudRepository.guardar(any(Solicitud.class)))
                .thenAnswer(invocation -> {
                    Solicitud solicitud = invocation.getArgument(0);
                    solicitud.setId("SOL-" + System.currentTimeMillis());
                    return Mono.just(solicitud);
                });

        when(notificationGateway.notificarSolicitudCreada(any()))
                .thenReturn(Mono.empty());

        when(eventPublisherGateway.publicarEventoSolicitudCreada(any()))
                .thenReturn(Mono.empty());
    }

    @Nested
    @DisplayName("Flujo Completo Exitoso")
    class FlujoCompletoExitoso {

        @Test
        @DisplayName("Debe procesar solicitud válida completamente")
        void debeProcesarSolicitudValidaCompletamente() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(result -> {
                        return result.getId() != null &&
                                result.getEstado() == EstadoSolicitud.PENDIENTE_REVISION &&
                                result.getNumeroDocumento().equals("12345678") &&
                                result.getEmail().equals("juan.perez@email.com");
                    })
                    .verifyComplete();

            // Verificar que se ejecutaron todas las operaciones
            verify(solicitudRepository).existeSolicitudActivaPorDocumento("12345678");
            verify(validacionExternaGateway).validarDocumento("12345678");
            verify(validacionExternaGateway).consultarHistorialCrediticio("12345678");
            verify(solicitudRepository).guardar(any(Solicitud.class));
            verify(notificationGateway).notificarSolicitudCreada(any(Solicitud.class));
            verify(eventPublisherGateway).publicarEventoSolicitudCreada(any(Solicitud.class));
        }

        @Test
        @DisplayName("Debe asignar estado inicial PENDIENTE_REVISION")
        void debeAsignarEstadoInicialPendienteRevision() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(result -> result.getEstado() == EstadoSolicitud.PENDIENTE_REVISION)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Validaciones de Formato Estrictas")
    class ValidacionesFormatoEstrictas {

        @Test
        @DisplayName("Debe rechazar email con formato inválido")
        void debeRechazarEmailFormatoInvalido() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setEmail("email-invalido");

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(DatosSolicitudInvalidosException.class)
                    .verify();
        }

        @Test
        @DisplayName("Debe rechazar teléfono con formato inválido")
        void debeRechazarTelefonoFormatoInvalido() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setTelefono("123456");

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(DatosSolicitudInvalidosException.class)
                    .verify();
        }

        @Test
        @DisplayName("Debe rechazar documento con caracteres no numéricos")
        void debeRechazarDocumentoNoNumerico() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setNumeroDocumento("123abc456");

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(DatosSolicitudInvalidosException.class)
                    .verify();
        }

        @Test
        @DisplayName("Debe rechazar nombres con caracteres especiales")
        void debeRechazarNombresConCaracteresEspeciales() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setNombres("Juan123");

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(DatosSolicitudInvalidosException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Validaciones de Negocio")
    class ValidacionesNegocio {

        @Test
        @DisplayName("Debe rechazar solicitud duplicada")
        void debeRechazarSolicitudDuplicada() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            when(solicitudRepository.existeSolicitudActivaPorDocumento("12345678"))
                    .thenReturn(Mono.just(true));

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(SolicitudDuplicadaException.class)
                    .verify();

            verify(solicitudRepository, never()).guardar(any());
        }

        @Test
        @DisplayName("Debe rechazar tipo de crédito inválido")
        void debeRechazarTipoCreditoInvalido() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setTipoCredito("INVALIDO");

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(DatosSolicitudInvalidosException.class)
                    .verify();
        }

        @Test
        @DisplayName("Debe rechazar monto fuera del rango permitido")
        void debeRechazarMontoFueraRango() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setMontoSolicitado(new BigDecimal("60000000")); // Mayor al máximo

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(DatosSolicitudInvalidosException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Manejo de Errores")
    class ManejoErrores {

        @Test
        @DisplayName("Debe manejar error en validación externa")
        void debeManejarErrorValidacionExterna() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            when(validacionExternaGateway.validarDocumento(anyString()))
                    .thenReturn(Mono.error(new RuntimeException("Error externo")));

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(RuntimeException.class)
                    .verify();
        }

        @Test
        @DisplayName("Debe continuar flujo si notificación falla")
        void debeContinuarFlujoSiNotificacionFalla() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            when(notificationGateway.notificarSolicitudCreada(any()))
                    .thenReturn(Mono.error(new RuntimeException("Error notificación")));

            // When & Then - El flujo debe continuar exitosamente
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(result -> result.getId() != null)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe continuar flujo si evento falla")
        void debeContinuarFlujoSiEventoFalla() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            when(eventPublisherGateway.publicarEventoSolicitudCreada(any()))
                    .thenReturn(Mono.error(new RuntimeException("Error evento")));

            // When & Then - El flujo debe continuar exitosamente
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(result -> result.getId() != null)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Escenarios de Borde")
    class EscenariosBorde {

        @Test
        @DisplayName("Debe manejar teléfono con código internacional")
        void debeManejarTelefonoCodigoInternacional() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setTelefono("+573001234567");

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(result -> result.getTelefono().equals("+573001234567"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe manejar nombres con acentos")
        void debeManejarNombresConAcentos() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setNombres("José María");
            solicitud.setApellidos("González Pérez");

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(result ->
                            result.getNombres().equals("José María") &&
                                    result.getApellidos().equals("González Pérez"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe manejar gastos mensuales null (opcional)")
        void debeManejarGastosMensualesNull() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setGastosMensuales(null);

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(result -> result.getGastosMensuales() == null)
                    .verifyComplete();
        }
    }

    // Métodos auxiliares
    private Solicitud crearSolicitudValida() {
        Solicitud solicitud = new Solicitud();
        solicitud.setNumeroDocumento("12345678");
        solicitud.setNombres("Juan Carlos");
        solicitud.setApellidos("Pérez González");
        solicitud.setEmail("juan.perez@email.com");
        solicitud.setTelefono("3001234567");
        solicitud.setMontoSolicitado(new BigDecimal("5000000"));
        solicitud.setPlazoMeses(24);
        solicitud.setTipoCredito("PERSONAL");
        solicitud.setIngresosMensuales(new BigDecimal("3000000"));
        solicitud.setGastosMensuales(new BigDecimal("1500000"));
        solicitud.setFechaCreacion(LocalDateTime.now());
        return solicitud;
    }
}