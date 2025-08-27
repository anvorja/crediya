package com.crediya.solicitudes.jpa.helper;

import com.crediya.solicitudes.jpa.SolicitudRepository;
import com.crediya.solicitudes.jpa.SolicitudRepositoryAdapter;
import com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private SolicitudRepository jpaRepository;

    private SolicitudRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new SolicitudRepositoryAdapter(jpaRepository);
    }

    @Test
    void testGuardar() {
        // Given
        SolicitudEntity mockEntity = createMockSolicitudEntity();
        Solicitud solicitud = createMockSolicitud();

        when(jpaRepository.save(any(SolicitudEntity.class))).thenReturn(mockEntity);

        // When
        Solicitud result = adapter.guardar(solicitud);

        // Then
        assertNotNull(result);
        assertEquals(mockEntity.getId(), result.getId());
    }

    @Test
    void testBuscarPorId() {
        // Given
        SolicitudEntity mockEntity = createMockSolicitudEntity();
        when(jpaRepository.findById("test-id")).thenReturn(Optional.of(mockEntity));

        // When
        Optional<Solicitud> result = adapter.buscarPorId("test-id");

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockEntity.getId(), result.get().getId());
    }

    @Test
    void testExisteSolicitudActivaPorDocumento() {
        // Given
        when(jpaRepository.existsSolicitudActivaByNumeroDocumento("12345678")).thenReturn(true);

        // When
        boolean result = adapter.existeSolicitudActivaPorDocumento("12345678");

        // Then
        assertTrue(result);
    }

    @Test
    void testListarTodas() {
        // Given
        List<SolicitudEntity> mockEntities = List.of(createMockSolicitudEntity());
        Page<SolicitudEntity> mockPage = new PageImpl<>(mockEntities);
        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        // When
        List<Solicitud> result = adapter.listarTodas(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testBuscarPorTermino() {
        // Given
        List<SolicitudEntity> mockEntities = List.of(createMockSolicitudEntity());
        Page<SolicitudEntity> mockPage = new PageImpl<>(mockEntities);
        when(jpaRepository.findByTermino(anyString(), any(Pageable.class))).thenReturn(mockPage);

        // When
        List<Solicitud> result = adapter.buscarPorTermino("Juan", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    private SolicitudEntity createMockSolicitudEntity() {
        return SolicitudEntity.builder()
                .id("test-id")
                .numeroDocumento("12345678")
                .nombres("Juan Carlos")
                .apellidos("Pérez Gómez")
                .email("juan.perez@email.com")
                .telefono("+573001234567")
                .montoSolicitado(new BigDecimal("5000000"))
                .tipoCredito("PERSONAL")
                .estado(SolicitudEntity.EstadoSolicitudEntity.PENDIENTE_REVISION)
                .fechaCreacion(LocalDateTime.now())
                .ingresosMensuales(new BigDecimal("3000000"))
                .gastosMensuales(new BigDecimal("1500000"))
                .build();
    }

    private Solicitud createMockSolicitud() {
        Solicitud solicitud = new Solicitud();
        solicitud.setId("test-id");
        solicitud.setNumeroDocumento("12345678");
        solicitud.setNombres("Juan Carlos");
        solicitud.setApellidos("Pérez Gómez");
        solicitud.setEmail("juan.perez@email.com");
        solicitud.setTelefono("+573001234567");
        solicitud.setMontoSolicitado(new BigDecimal("5000000"));
        solicitud.setTipoCredito("PERSONAL");
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        solicitud.setFechaCreacion(LocalDateTime.now());
        solicitud.setIngresosMensuales(new BigDecimal("3000000"));
        solicitud.setGastosMensuales(new BigDecimal("1500000"));
        return solicitud;
    }
}