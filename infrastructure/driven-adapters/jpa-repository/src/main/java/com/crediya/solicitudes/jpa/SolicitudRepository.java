// infrastructure/driven-adapters/jpa-repository/src/main/java/com/crediya/solicitudes/jpa/SolicitudRepository.java
package com.crediya.solicitudes.jpa;

import com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudEntity, String> {

    /**
     * Busca la solicitud más reciente por número de documento
     */
    Optional<SolicitudEntity> findByNumeroDocumento(String numeroDocumento);

    /**
     * Busca TODAS las solicitudes por número de documento (historial completo)
     * Ordenadas por fecha de creación descendente
     */
    List<SolicitudEntity> findAllByNumeroDocumentoOrderByFechaCreacionDesc(String numeroDocumento);

    /**
     * Lista solicitudes por estado
     */
    List<SolicitudEntity> findByEstado(SolicitudEntity.EstadoSolicitudEntity estado);

    /**
     * Verifica si existe una solicitud activa (no final) por documento
     * Estados activos: PENDIENTE_REVISION, EN_REVISION
     * Estados finales: APROBADA, RECHAZADA
     */
    @Query("SELECT COUNT(s) > 0 FROM SolicitudEntity s WHERE s.numeroDocumento = :numeroDocumento " +
            "AND s.estado IN ('PENDIENTE_REVISION', 'EN_REVISION')")
    boolean existsSolicitudActivaByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    /**
     * Cuenta solicitudes por estado
     */
    long countByEstado(SolicitudEntity.EstadoSolicitudEntity estado);

    /**
     * Lista solicitudes por rango de fechas
     */
    List<SolicitudEntity> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca solicitudes por término en nombres, apellidos o número de documento
     */
    @Query("SELECT s FROM SolicitudEntity s WHERE " +
            "LOWER(s.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(s.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "s.numeroDocumento LIKE CONCAT('%', :termino, '%')")
    Page<SolicitudEntity> findByTermino(@Param("termino") String termino, Pageable pageable);

    /**
     * Lista todas las solicitudes paginadas
     */
    Page<SolicitudEntity> findAll(Pageable pageable);
}