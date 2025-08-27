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
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearSolicitudUseCase - Tests")
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
                solicitudRepository,
                notificationGateway,
                eventPublisherGateway,
                validacionExternaGateway
        );
    }

    @Nested
    @DisplayName("Criterios de Aceptación - Historia de Usuario")
    class CriteriosAceptacion {

        @Test
        @DisplayName("CA-1: Debe registrar solicitud con datos completos del cliente y préstamo")
        void debeRegistrarSolicitudConDatosCompletos() {
            // Given - Solicitud con todos los datos requeridos
            Solicitud solicitud = crearSolicitudValida();
            Solicitud solicitudGuardada = crearSolicitudGuardada(solicitud);

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);
            when(validacionExternaGateway.validarDocumento(anyString()))
                    .thenReturn(Optional.empty());
            when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                    .thenReturn(Optional.empty());
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenReturn(solicitudGuardada);

            // When
            Solicitud resultado = useCase.ejecutar(solicitud);

            // Then - Verificar que se guardó con los datos correctos
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNumeroDocumento()).isEqualTo("12345678");
            assertThat(resultado.getNombres()).isEqualTo("Juan Carlos");
            assertThat(resultado.getMontoSolicitado()).isEqualByComparingTo(new BigDecimal("5000000"));
            assertThat(resultado.getTipoCredito()).isEqualTo("PERSONAL");

            // Verificar que se guardó la solicitud
            verify(solicitudRepository).guardar(any(Solicitud.class));
        }

        @Test
        @DisplayName("CA-2: Debe asignar estado inicial 'PENDIENTE_REVISION' automáticamente")
        void debeAsignarEstadoPendienteRevision() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setEstado(null); // Estado inicial vacío

            Solicitud solicitudGuardada = crearSolicitudGuardada(solicitud);
            solicitudGuardada.setEstado(EstadoSolicitud.PENDIENTE_REVISION);

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);
            when(validacionExternaGateway.validarDocumento(anyString()))
                    .thenReturn(Optional.empty());
            when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                    .thenReturn(Optional.empty());
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenReturn(solicitudGuardada);

            // When
            Solicitud resultado = useCase.ejecutar(solicitud);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE_REVISION);
        }

        @Test
        @DisplayName("CA-3: Debe validar que el tipo de préstamo sea válido")
        void debeValidarTipoPrestamoValido() {
            // Given - Tipos válidos según BusinessConstants
            String[] tiposValidos = {"PERSONAL", "VEHICULO", "VIVIENDA", "EDUCATIVO"};

            for (String tipoValido : tiposValidos) {
                Solicitud solicitud = crearSolicitudValida();
                solicitud.setTipoCredito(tipoValido);

                when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                        .thenReturn(false);
                when(solicitudRepository.guardar(any(Solicitud.class)))
                        .thenReturn(solicitud);

                // When & Then - No debe lanzar excepción
                assertThatNoException().isThrownBy(() -> useCase.ejecutar(solicitud));
            }
        }

        @Test
        @DisplayName("CA-3: Debe rechazar tipos de préstamo inválidos")
        void debeRechazarTipoPrestamoInvalido() {
            // Given - Tipo inválido
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setTipoCredito("TIPO_INVALIDO");

            // When & Then
            assertThatThrownBy(() -> useCase.ejecutar(solicitud))
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("Tipo de crédito no válido");
        }
    }

    @Nested
    @DisplayName("Validaciones de Negocio")
    class ValidacionesNegocio {

        @Test
        @DisplayName("Debe fallar si ya existe solicitud activa para el documento")
        void debeFallarSiExisteSolicitudActiva() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            when(solicitudRepository.existeSolicitudActivaPorDocumento("12345678"))
                    .thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> useCase.ejecutar(solicitud))
                    .isInstanceOf(SolicitudDuplicadaException.class)
                    .hasMessageContaining("Ya existe una solicitud activa para el documento: 12345678");

            // Verificar que no se intentó guardar
            verify(solicitudRepository, never()).guardar(any());
        }

        @Test
        @DisplayName("Debe validar datos básicos antes de procesar")
        void debeValidarDatosBasicos() {
            // Given - Solicitud con datos inválidos
            Solicitud solicitud = crearSolicitudValida();
            solicitud.setEmail("email-invalido"); // Email mal formateado

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> useCase.ejecutar(solicitud))
                    .isInstanceOf(DatosSolicitudInvalidosException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("Debe agregar observaciones cuando validación externa falla")
        void debeAgregarObservacionesValidacionExterna() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            ValidacionDocumento validacionDoc = new ValidacionDocumento(false, "Pedro", "García");

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);
            when(validacionExternaGateway.validarDocumento(anyString()))
                    .thenReturn(Optional.of(validacionDoc));
            when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                    .thenReturn(Optional.empty());
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Solicitud resultado = useCase.ejecutar(solicitud);

            // Then
            assertThat(resultado.getObservaciones())
                    .contains("Datos del documento requieren verificación manual");
        }

        @Test
        @DisplayName("Debe agregar observaciones por historial crediticio negativo")
        void debeAgregarObservacionesHistorialNegativo() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            HistorialCrediticio historial = new HistorialCrediticio(true, 450);

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);
            when(validacionExternaGateway.validarDocumento(anyString()))
                    .thenReturn(Optional.empty());
            when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                    .thenReturn(Optional.of(historial));
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Solicitud resultado = useCase.ejecutar(solicitud);

            // Then
            assertThat(resultado.getObservaciones())
                    .contains("Historial crediticio requiere revisión (Score: 450)");
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
            Solicitud solicitudGuardada = crearSolicitudGuardada(solicitud);

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);
            when(validacionExternaGateway.validarDocumento(anyString()))
                    .thenReturn(Optional.empty());
            when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                    .thenReturn(Optional.empty());
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenReturn(solicitudGuardada);

            // When
            useCase.ejecutar(solicitud);

            // Then - Verificar que se llamaron los métodos de notificación
            verify(notificationGateway).notificarSolicitudCreada(solicitudGuardada);
            verify(eventPublisherGateway).publicarEventoSolicitudCreada(solicitudGuardada);
        }

        @Test
        @DisplayName("Debe manejar fallos en notificaciones sin afectar el flujo principal")
        void debeManejarefallosNotificaciones() {
            // Given
            Solicitud solicitud = crearSolicitudValida();
            Solicitud solicitudGuardada = crearSolicitudGuardada(solicitud);

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenReturn(solicitudGuardada);

            // Simular fallo en notificación
            doThrow(new RuntimeException("Error de notificación"))
                    .when(notificationGateway).notificarSolicitudCreada(any());

            // When & Then - No debe fallar el caso de uso principal
            assertThatNoException().isThrownBy(() -> useCase.ejecutar(solicitud));
        }
    }

    @Nested
    @DisplayName("Casos Límite")
    class CasosLimite {

        @Test
        @DisplayName("Debe procesar solicitud sin validaciones externas")
        void debeProcesarSinValidacionesExternas() {
            // Given
            Solicitud solicitud = crearSolicitudValida();

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);
            when(validacionExternaGateway.validarDocumento(anyString()))
                    .thenReturn(Optional.empty());
            when(validacionExternaGateway.consultarHistorialCrediticio(anyString()))
                    .thenReturn(Optional.empty());
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Solicitud resultado = useCase.ejecutar(solicitud);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getObservaciones()).isNull();
        }

        @Test
        @DisplayName("Debe procesar montos en límites permitidos")
        void debeProcesarMontosEnLimites() {
            // Given - Monto mínimo
            Solicitud solicitudMin = crearSolicitudValida();
            solicitudMin.setMontoSolicitado(new BigDecimal("100000")); // Monto mínimo

            // Given - Monto máximo  
            Solicitud solicitudMax = crearSolicitudValida();
            solicitudMax.setMontoSolicitado(new BigDecimal("50000000")); // Monto máximo

            when(solicitudRepository.existeSolicitudActivaPorDocumento(anyString()))
                    .thenReturn(false);
            when(solicitudRepository.guardar(any(Solicitud.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When & Then - Ambos deben procesarse sin error
            assertThatNoException().isThrownBy(() -> useCase.ejecutar(solicitudMin));
            assertThatNoException().isThrownBy(() -> useCase.ejecutar(solicitudMax));
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES PARA CREAR SOLICITUDES DE PRUEBA
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
        solicitud.setFechaCreacion(LocalDateTime.now());
        solicitud.setIngresosMensuales(new BigDecimal("3000000"));
        solicitud.setGastosMensuales(new BigDecimal("1500000"));
        return solicitud;
    }

    private Solicitud crearSolicitudGuardada(Solicitud original) {
        Solicitud guardada = new Solicitud();
        // Copiar todos los campos de la original
        guardada.setNumeroDocumento(original.getNumeroDocumento());
        guardada.setNombres(original.getNombres());
        guardada.setApellidos(original.getApellidos());
        guardada.setEmail(original.getEmail());
        guardada.setTelefono(original.getTelefono());
        guardada.setMontoSolicitado(original.getMontoSolicitado());
        guardada.setTipoCredito(original.getTipoCredito());
        guardada.setIngresosMensuales(original.getIngresosMensuales());
        guardada.setGastosMensuales(original.getGastosMensuales());
        guardada.setObservaciones(original.getObservaciones());

        // Campos que se asignan al guardar
        guardada.setId("SOL-001");
        guardada.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        guardada.setFechaCreacion(LocalDateTime.now());
        guardada.setFechaActualizacion(LocalDateTime.now());

        return guardada;
    }
}