// infrastructure/driven-adapters/jpa-repository/src/main/java/com/crediya/solicitudes/jpa/SolicitudRepositoryAdapter.java
package com.crediya.solicitudes.jpa;

import com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import com.crediya.solicitudes.jpa.mapper.SolicitudEntityMapper;
import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación JPA del gateway SolicitudRepository del dominio
 * ADAPTADOR que conecta el dominio puro con la infraestructura JPA
 */
@Slf4j
@Component
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private final com.crediya.solicitudes.jpa.SolicitudRepository jpaRepository;
    private final SolicitudEntityMapper mapper;

    public SolicitudRepositoryAdapter(com.crediya.solicitudes.jpa.SolicitudRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.mapper = SolicitudEntityMapper.INSTANCE;
    }

    @Override
    public Solicitud guardar(Solicitud solicitud) {
        try {
            log.debug("💾 Guardando solicitud: {}", solicitud.getId());

            SolicitudEntity entity = mapper.toEntity(solicitud);
            SolicitudEntity savedEntity = jpaRepository.save(entity);

            log.debug("✅ Solicitud guardada exitosamente: {}", savedEntity.getId());
            return mapper.toDomain(savedEntity);

        } catch (Exception e) {
            log.error("❌ Error guardando solicitud {}: {}", solicitud.getId(), e.getMessage());
            throw new RuntimeException("Error guardando solicitud: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Solicitud> buscarPorId(String id) {
        try {
            log.debug("🔍 Buscando solicitud por ID: {}", id);

            return jpaRepository.findById(id)
                    .map(entity -> {
                        log.debug("✅ Solicitud encontrada: {}", entity.getId());
                        return mapper.toDomain(entity);
                    });

        } catch (Exception e) {
            log.error("❌ Error buscando solicitud por ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Solicitud> buscarPorNumeroDocumento(String numeroDocumento) {
        try {
            log.debug("🔍 Buscando solicitud por documento: {}", numeroDocumento);

            return jpaRepository.findByNumeroDocumento(numeroDocumento)
                    .map(entity -> {
                        log.debug("✅ Solicitud encontrada para documento {}: {}",
                                numeroDocumento, entity.getId());
                        return mapper.toDomain(entity);
                    });

        } catch (Exception e) {
            log.error("❌ Error buscando solicitud por documento {}: {}", numeroDocumento, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Solicitud> buscarPorDocumento(String numeroDocumento) {
        try {
            log.debug("🔍 Buscando TODAS las solicitudes por documento: {}", numeroDocumento);

            // Para buscar todas las solicitudes históricas por documento
            // Necesitamos una query personalizada en el repositorio JPA
            List<SolicitudEntity> entities = jpaRepository.findAllByNumeroDocumentoOrderByFechaCreacionDesc(numeroDocumento);

            List<Solicitud> solicitudes = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("✅ Encontradas {} solicitudes para documento {}", solicitudes.size(), numeroDocumento);
            return solicitudes;

        } catch (Exception e) {
            log.error("❌ Error buscando solicitudes por documento {}: {}", numeroDocumento, e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean existeSolicitudActivaPorDocumento(String numeroDocumento) {
        try {
            log.debug("🔍 Verificando solicitud activa para documento: {}", numeroDocumento);

            boolean existe = jpaRepository.existsSolicitudActivaByNumeroDocumento(numeroDocumento);

            log.debug("✅ Solicitud activa para documento {}: {}", numeroDocumento, existe ? "SÍ" : "NO");
            return existe;

        } catch (Exception e) {
            log.error("❌ Error verificando solicitud activa para documento {}: {}", numeroDocumento, e.getMessage());
            return false;
        }
    }

    @Override
    public List<Solicitud> buscarPorEstado(EstadoSolicitud estado) {
        try {
            log.debug("🔍 Buscando solicitudes por estado: {}", estado);

            SolicitudEntity.EstadoSolicitudEntity estadoEntity = mapper.mapEstadoToEntity(estado);
            List<SolicitudEntity> entities = jpaRepository.findByEstado(estadoEntity);

            List<Solicitud> solicitudes = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("✅ Encontradas {} solicitudes en estado {}", solicitudes.size(), estado);
            return solicitudes;

        } catch (Exception e) {
            log.error("❌ Error buscando solicitudes por estado {}: {}", estado, e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Solicitud> buscarTodas() {
        try {
            log.debug("🔍 Listando todas las solicitudes (sin paginación)");

            List<SolicitudEntity> entities = jpaRepository.findAll(
                    Sort.by(Sort.Direction.DESC, "fechaCreacion")
            );

            List<Solicitud> solicitudes = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("✅ Encontradas {} solicitudes totales", solicitudes.size());
            return solicitudes;

        } catch (Exception e) {
            log.error("❌ Error listando todas las solicitudes: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Solicitud> listarTodas(int pagina, int tamanio) {
        try {
            log.debug("🔍 Listando solicitudes paginadas: página {} tamaño {}", pagina, tamanio);

            PageRequest pageRequest = PageRequest.of(pagina, tamanio,
                    Sort.by(Sort.Direction.DESC, "fechaCreacion"));

            List<SolicitudEntity> entities = jpaRepository.findAll(pageRequest)
                    .getContent();

            List<Solicitud> solicitudes = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("✅ Página {} con {} solicitudes", pagina, solicitudes.size());
            return solicitudes;

        } catch (Exception e) {
            log.error("❌ Error listando solicitudes paginadas: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public long contarPorEstado(EstadoSolicitud estado) {
        try {
            log.debug("🔍 Contando solicitudes por estado: {}", estado);

            SolicitudEntity.EstadoSolicitudEntity estadoEntity = mapper.mapEstadoToEntity(estado);
            long count = jpaRepository.countByEstado(estadoEntity);

            log.debug("✅ Total solicitudes en estado {}: {}", estado, count);
            return count;

        } catch (Exception e) {
            log.error("❌ Error contando solicitudes por estado {}: {}", estado, e.getMessage());
            return 0L;
        }
    }

    @Override
    public List<Solicitud> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        try {
            log.debug("🔍 Listando solicitudes por rango: {} a {}", fechaInicio, fechaFin);

            List<SolicitudEntity> entities = jpaRepository.findByFechaCreacionBetween(fechaInicio, fechaFin);

            List<Solicitud> solicitudes = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("✅ Encontradas {} solicitudes en el rango de fechas", solicitudes.size());
            return solicitudes;

        } catch (Exception e) {
            log.error("❌ Error listando por rango de fechas: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Solicitud> buscarPorTermino(String termino, int pagina, int tamanio) {
        try {
            log.debug("🔍 Buscando solicitudes por término '{}': página {} tamaño {}", termino, pagina, tamanio);

            PageRequest pageRequest = PageRequest.of(pagina, tamanio,
                    Sort.by(Sort.Direction.DESC, "fechaCreacion"));

            List<SolicitudEntity> entities = jpaRepository.findByTermino(termino, pageRequest)
                    .getContent();

            List<Solicitud> solicitudes = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("✅ Encontradas {} solicitudes para término '{}'", solicitudes.size(), termino);
            return solicitudes;

        } catch (Exception e) {
            log.error("❌ Error buscando por término '{}': {}", termino, e.getMessage());
            return List.of();
        }
    }

    @Override
    public void eliminar(String id) {
        try {
            log.debug("🗑️ Eliminando solicitud: {}", id);

            if (jpaRepository.existsById(id)) {
                jpaRepository.deleteById(id);
                log.debug("✅ Solicitud {} eliminada exitosamente", id);
            } else {
                log.warn("⚠️ Solicitud {} no encontrada para eliminar", id);
            }

        } catch (Exception e) {
            log.error("❌ Error eliminando solicitud {}: {}", id, e.getMessage());
            throw new RuntimeException("Error eliminando solicitud: " + e.getMessage(), e);
        }
    }
}