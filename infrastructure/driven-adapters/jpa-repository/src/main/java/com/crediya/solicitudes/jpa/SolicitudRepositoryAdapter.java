// infrastructure/driven-adapters/jpa-repository/src/main/java/com/crediya/solicitudes/jpa/JPARepositoryAdapter.java
package com.crediya.solicitudes.jpa;

import com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import com.crediya.solicitudes.jpa.mapper.SolicitudEntityMapper;
import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
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
@Component
public class SolicitudRepositoryAdapter implements com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository {

    private final SolicitudRepository jpaRepository;
    private final SolicitudEntityMapper mapper;

    public SolicitudRepositoryAdapter(SolicitudRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.mapper = SolicitudEntityMapper.INSTANCE;
    }

    @Override
    public Solicitud guardar(Solicitud solicitud) {
        SolicitudEntity entity = mapper.toEntity(solicitud);
        SolicitudEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Solicitud> buscarPorId(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Solicitud> buscarPorNumeroDocumento(String numeroDocumento) {
        return jpaRepository.findByNumeroDocumento(numeroDocumento)
                .map(mapper::toDomain);
    }

    @Override
    public List<Solicitud> listarPorEstado(EstadoSolicitud estado) {
        SolicitudEntity.EstadoSolicitudEntity estadoEntity = mapper.mapEstadoToEntity(estado);
        return jpaRepository.findByEstado(estadoEntity)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Solicitud> listarTodas(int pagina, int tamanio) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanio,
                Sort.by(Sort.Direction.DESC, "fechaCreacion"));

        return jpaRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeSolicitudActivaPorDocumento(String numeroDocumento) {
        return jpaRepository.existsSolicitudActivaByNumeroDocumento(numeroDocumento);
    }

    @Override
    public long contarPorEstado(EstadoSolicitud estado) {
        SolicitudEntity.EstadoSolicitudEntity estadoEntity = mapper.mapEstadoToEntity(estado);
        return jpaRepository.countByEstado(estadoEntity);
    }

    @Override
    public List<Solicitud> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return jpaRepository.findByFechaCreacionBetween(fechaInicio, fechaFin)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Solicitud> buscarPorTermino(String termino, int pagina, int tamanio) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanio,
                Sort.by(Sort.Direction.DESC, "fechaCreacion"));

        return jpaRepository.findByTermino(termino, pageRequest)
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}