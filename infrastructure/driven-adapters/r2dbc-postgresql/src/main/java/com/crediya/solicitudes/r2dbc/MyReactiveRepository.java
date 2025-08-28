// infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/com/crediya/solicitudes/r2dbc/MyReactiveRepository.java
package com.crediya.solicitudes.r2dbc;

import com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository R2DBC REACTIVO para SolicitudEntity
 * Reemplaza MyReactiveRepository generado por el scaffold
 */
@Repository
public interface MyReactiveRepository extends R2dbcRepository<SolicitudEntity, String>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

    // ========================================
    // MÉTODOS DE CONSULTA REACTIVOS
    // ========================================

    /**
     * Busca la solicitud más reciente por número de documento
     */
    Mono<SolicitudEntity> findTopByNumeroDocumentoOrderByFechaCreacionDesc(String numeroDocumento);

    /**
     * Busca todas las solicitudes por número de documento ordenadas por fecha
     */
    Flux<SolicitudEntity> findAllByNumeroDocumentoOrderByFechaCreacionDesc(String numeroDocumento);

    /**
     * Verifica si existe una solicitud activa para un documento
     */
    @Query("SELECT COUNT(*) > 0 FROM solicitudes WHERE numero_documento = :numeroDocumento AND estado IN (:estados)")
    Mono<Boolean> existsByNumeroDocumentoAndEstadoIn(String numeroDocumento, List<String> estados);

    /**
     * Busca solicitudes por estado ordenadas por fecha
     */
    @Query("SELECT * FROM solicitudes WHERE estado = :estado ORDER BY fecha_creacion DESC")
    Flux<SolicitudEntity> findByEstadoOrderByFechaCreacionDesc(String estado);

    /**
     * Lista todas las solicitudes ordenadas por fecha
     */
    Flux<SolicitudEntity> findAllByOrderByFechaCreacionDesc();

    /**
     * Cuenta solicitudes por estado
     */
    @Query("SELECT COUNT(*) FROM solicitudes WHERE estado = :estado")
    Mono<Long> countByEstado(String estado);

    /**
     * Busca solicitudes por rango de fechas
     */
    Flux<SolicitudEntity> findByFechaCreacionBetweenOrderByFechaCreacionDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca por múltiples estados
     */
    @Query("SELECT * FROM solicitudes WHERE estado IN (:estados) ORDER BY fecha_creacion DESC")
    Flux<SolicitudEntity> findByEstadoInOrderByFechaCreacionDesc(List<String> estados);

    /**
     * Búsqueda por término (nombres, apellidos, documento)
     */
    @Query("SELECT * FROM solicitudes WHERE " +
            "LOWER(nombres) LIKE :termino OR " +
            "LOWER(apellidos) LIKE :termino OR " +
            "numero_documento LIKE :numeroDocumento " +
            "ORDER BY fecha_creacion DESC")
    Flux<SolicitudEntity> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrNumeroDocumentoContaining(
            String termino, String termino2, String numeroDocumento);
}