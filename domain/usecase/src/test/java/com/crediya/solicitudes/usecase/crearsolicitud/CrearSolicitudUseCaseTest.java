// domain/usecase/src/test/java/com/crediya/solicitudes/usecase/crearsolicitud/CrearSolicitudUseCaseTest.java
package com.crediya.solicitudes.usecase.crearsolicitud;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.exception.DatosSolicitudInvalidosException;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CrearSolicitudUseCase - Tests Reactivos")
class CrearSolicitudUseCaseTest {

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
    }

    @Nested
    @DisplayName("Criterios de Aceptación - Historia de Usuario")
    class CriteriosAceptacion {

        @Test
        @DisplayName("CA-1: Debe registrar solicitud con datos completos del cliente y préstamo")
        void debeRegistrarSolicitudConDatosCompletos() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // Usar helper method para todos los mocks
            setupMocksCompletos();

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(resultado -> {
                        assertThat(resultado.getNumeroDocumento()).isEqualTo("12345678");
                        assertThat(resultado.getNombres()).isEqualTo("Juan Carlos");
                        assertThat(resultado.getMontoSolicitado()).isEqualByComparingTo(new BigDecimal("5000000"));
                        assertThat(resultado.getTipoCredito()).isEqualTo("PERSONAL");
                        return true;
                    })
                    .verifyComplete();

            // Verificar interacciones
            verify(solicitudRepository).guardar(any(Solicitud.class));
        }

        @Test
        @DisplayName("CA-2: Debe asignar estado inicial 'PENDIENTE_REVISION' automáticamente")
        void debeAsignarEstadoPendienteRevision() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // Usar helper method para todos los mocks
            setupMocksCompletos();

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(resultado ->
                            resultado.getEstado() == EstadoSolicitud.PENDIENTE_REVISION)
                    .verifyComplete();
        }

        @Test
        @DisplayName("CA-3: Debe validar que el tipo de préstamo sea válido")
        void debeValidarTipoPrestamoValido() {
            // Given - Usar tipos válidos de BusinessConstants
            String[] tiposValidos = {"PERSONAL", "VEHICULO", "VIVIENDA", "EDUCATIVO"};

            for (String tipoValido : tiposValidos) {
                Solicitud solicitud = crearSolicitudValida();
                solicitud.setTipoCredito(tipoValido);

                // Usar helper method para todos los mocks
                setupMocksCompletos();

                // When & Then - No debe lanzar excepción
                StepVerifier.create(useCase.ejecutar(solicitud))
                        .expectNextCount(1)
                        .verifyComplete();
            }
        }

        @Test
        @DisplayName("CA-3: Debe rechazar tipos de préstamo inválidos")
        void debeRechazarTipoPrestamoInvalido() {
            // Given - Tipo inválido
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setTipoCredito("TIPO_INVALIDO");

            // AGREGAR MOCKS BÁSICOS para que el flujo llegue hasta la validación
            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(Mono.just(false));

            // When & Then - Debe fallar inmediatamente en validación
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectError(DatosSolicitudInvalidosException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Validaciones de Negocio")
    class ValidacionesNegocio {

        @Test
        @DisplayName("Debe agregar observaciones por historial crediticio negativo")
        void debeAgregarObservacionesPorHistorialNegativo() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // Usar helper method base y sobrescribir específico
            setupMocksCompletos();

            // Sobrescribir solo el mock específico para historial negativo
            when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                    .thenReturn(Mono.just(new HistorialCrediticio(true, 450))); // historial negativo

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(resultado ->
                            resultado.getObservaciones() != null &&
                                    resultado.getObservaciones().contains("Historial crediticio negativo")
                    )
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe agregar observaciones cuando validación externa falla")
        void debeAgregarObservacionesPorValidacionExternaFallida() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // Usar helper method base y sobrescribir específico
            setupMocksCompletos();

            // Sobrescribir solo el mock específico para validación financiera fallida
            when(validacionExternaGateway.validarInformacionFinanciera(anyString(), any()))
                    .thenReturn(Mono.just(new ValidacionFinanciera(false, BigDecimal.ZERO, "ERROR", "Datos inconsistentes")));

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(resultado ->
                            resultado.getObservaciones() != null &&
                                    resultado.getObservaciones().contains("Validación financiera falló")
                    )
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Interacciones con Gateways")
    class InteraccionesGateways {

        @Test
        @DisplayName("Debe notificar y publicar eventos después de guardar")
        void debeNotificarYPublicarEventos() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // Usar helper method para todos los mocks
            setupMocksCompletos();

            // When
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextCount(1)
                    .verifyComplete();

            // Then
            verify(notificationGateway).notificarSolicitudCreada(any());
            verify(eventPublisherGateway).publicarEventoSolicitudCreada(any());
        }

        @Test
        @DisplayName("Debe manejar fallos en notificaciones sin afectar el flujo principal")
        void debeManejarFallosEnNotificaciones() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // Usar helper method base y sobrescribir específico
            setupMocksCompletos();

            // Sobrescribir solo el mock de notificación para que falle
            when(notificationGateway.notificarSolicitudCreada(any()))
                    .thenReturn(Mono.error(new RuntimeException("Error en notificación")));

            // When & Then - El flujo debe continuar aunque falle la notificación
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextCount(1)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Casos Límite")
    class CasosLimite {

        @Test
        @DisplayName("Debe procesar montos en límites permitidos")
        void debeProcesarMontosEnLimitesPermitidos() {
            // Given - Monto en el límite superior
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setMontoSolicitado(new BigDecimal("50000000")); // 50M límite superior

            // Usar helper method para todos los mocks
            setupMocksCompletos();

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(resultado ->
                            resultado.getMontoSolicitado().compareTo(new BigDecimal("50000000")) == 0
                    )
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe procesar solicitud sin validaciones externas complejas")
        void debeProcesarSolicitudSinValidacionesExternas() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            // Usar helper method para todos los mocks
            setupMocksCompletos();

            // When & Then
            StepVerifier.create(useCase.ejecutar(solicitud))
                    .expectNextMatches(resultado ->
                            resultado.getEstado() == EstadoSolicitud.PENDIENTE_REVISION
                    )
                    .verifyComplete();
        }
    }

    // Métodos helper adaptados a tu modelo SIN LOMBOK
    private Solicitud crearSolicitudValida() {
        Solicitud solicitud = new Solicitud();
        solicitud.setNumeroDocumento("12345678");
        solicitud.setNombres("Juan Carlos");
        solicitud.setApellidos("García López");
        solicitud.setTelefono("3001234567");
        solicitud.setEmail("juan.garcia@email.com");
        solicitud.setIngresosMensuales(new BigDecimal("3000000"));
        solicitud.setGastosMensuales(new BigDecimal("1500000"));
        solicitud.setMontoSolicitado(new BigDecimal("5000000"));
        solicitud.setPlazoMeses(24);
        solicitud.setTipoCredito("PERSONAL");
        return solicitud;
    }

    private Solicitud crearSolicitudGuardada(Solicitud original) {
        Solicitud guardada = new Solicitud();
        guardada.setId("SOL-" + System.currentTimeMillis());
        guardada.setNumeroDocumento(original.getNumeroDocumento());
        guardada.setNombres(original.getNombres());
        guardada.setApellidos(original.getApellidos());
        guardada.setTelefono(original.getTelefono());
        guardada.setEmail(original.getEmail());
        guardada.setIngresosMensuales(original.getIngresosMensuales());
        guardada.setGastosMensuales(original.getGastosMensuales());
        guardada.setMontoSolicitado(original.getMontoSolicitado());
        guardada.setPlazoMeses(original.getPlazoMeses());
        guardada.setTipoCredito(original.getTipoCredito());
        guardada.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        guardada.setFechaCreacion(LocalDateTime.now());
        return guardada;
    }

    // HELPER METHOD PARA CONFIGURAR TODOS LOS MOCKS NECESARIOS
    private void setupMocksCompletos() {
        when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                .thenReturn(Mono.just(false));
        when(validacionExternaGateway.validarDocumento(anyString()))
                .thenReturn(Mono.just(new ValidacionDocumento(true, "Juan", "Pérez")));
        when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                .thenReturn(Mono.just(new HistorialCrediticio(false, 650)));
        when(validacionExternaGateway.validarInformacionFinanciera(anyString(), any()))
                .thenReturn(Mono.just(new ValidacionFinanciera(true, new BigDecimal("3000000"), "MOCK", "OK")));
        when(solicitudRepository.guardar(any(Solicitud.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(notificationGateway.notificarSolicitudCreada(any()))
                .thenReturn(Mono.empty());
        when(eventPublisherGateway.publicarEventoSolicitudCreada(any()))
                .thenReturn(Mono.empty());
    }
}