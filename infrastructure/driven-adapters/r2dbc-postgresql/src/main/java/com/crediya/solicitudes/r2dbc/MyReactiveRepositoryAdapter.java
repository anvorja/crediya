// infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/com/crediya/solicitudes/r2dbc/MyReactiveRepositoryAdapter.java
package com.crediya.solicitudes.r2dbc;

import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import com.crediya.solicitudes.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Adapter R2DBC que implementa SolicitudRepository del dominio
 * Utiliza la clase base ReactiveAdapterOperations del scaffold
 */
@Slf4j
@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud, // Domain model
        SolicitudEntity, // Adapter model
        String, // ID type
        MyReactiveRepository
        > implements SolicitudRepository {

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, solicitudEntity -> mapper.map(solicitudEntity, Solicitud.class));
    }

    // ========================================
    // IMPLEMENTACIÓN DE SolicitudRepository
    // ========================================

    @Override
    public Mono<Solicitud> guardar(Solicitud solicitud) {
        return Mono.fromCallable(() -> {
                    SolicitudEntity entity = mapper.map(solicitud, SolicitudEntity.class);
                    entity.setId(null); // FORZAR INSERT quitando el ID
                    return entity;
                })
                .flatMap(repository::save)
                .map(savedEntity -> mapper.map(savedEntity, Solicitud.class))
                .doOnNext(s -> log.debug("Solicitud guardada: {}", s.getId()))
                .onErrorMap(Exception.class, ex -> {
                    log.error("Error guardando solicitud: {}", ex.getMessage());
                    return new RuntimeException("Error guardando solicitud", ex);
                });
    }

    @Override
    public Mono<Solicitud> buscarPorId(String id) {
        return findById(id)
                .doOnNext(s -> log.debug("🔍 Solicitud encontrada: {}", id))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error buscando solicitud por ID {}: {}", id, ex.getMessage());
                    return new RuntimeException("Error buscando solicitud", ex);
                });
    }

    @Override
    public Mono<Solicitud> buscarPorNumeroDocumento(String numeroDocumento) {
        return repository.findTopByNumeroDocumentoOrderByFechaCreacionDesc(numeroDocumento)
                .map(this::toEntity)
                .doOnNext(s -> log.debug("🔍 Solicitud encontrada para documento: {}", numeroDocumento))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error buscando por documento {}: {}", numeroDocumento, ex.getMessage());
                    return new RuntimeException("Error buscando solicitud por documento", ex);
                });
    }

    @Override
    public Flux<Solicitud> buscarPorDocumento(String numeroDocumento) {
        return repository.findAllByNumeroDocumentoOrderByFechaCreacionDesc(numeroDocumento)
                .map(this::toEntity)
                .doOnNext(s -> log.debug("🔍 Solicitud histórica: {} para documento: {}", s.getId(), numeroDocumento))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error buscando historial para documento {}: {}", numeroDocumento, ex.getMessage());
                    return new RuntimeException("Error buscando historial de solicitudes", ex);
                });
    }

    @Override
    public Mono<Boolean> existeSolicitudActivaPorDocumento(String numeroDocumento) {
        return repository.existsByNumeroDocumentoAndEstadoIn(
                        numeroDocumento,
                        java.util.List.of("PENDIENTE_REVISION", "EN_REVISION")
                )
                .doOnNext(existe -> log.debug("🔍 Solicitud activa para documento {}: {}", numeroDocumento,
                        existe ? "SÍ" : "NO"))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error verificando solicitud activa para documento {}: {}",
                            numeroDocumento, ex.getMessage());
                    return new RuntimeException("Error verificando solicitud activa", ex);
                });
    }

    @Override
    public Flux<Solicitud> buscarPorEstado(EstadoSolicitud estado) {
        String estadoString = estado.name();
        return repository.findByEstadoOrderByFechaCreacionDesc(estadoString)
                .map(this::toEntity)
                .doOnNext(s -> log.debug("🔍 Solicitud en estado {}: {}", estado, s.getId()))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error buscando por estado {}: {}", estado, ex.getMessage());
                    return new RuntimeException("Error buscando solicitudes por estado", ex);
                });
    }

    @Override
    public Flux<Solicitud> buscarTodas() {
        return findAll()
                .doOnNext(s -> log.debug("🔍 Solicitud: {}", s.getId()))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error listando todas las solicitudes: {}", ex.getMessage());
                    return new RuntimeException("Error listando solicitudes", ex);
                });
    }

    @Override
    public Flux<Solicitud> listarTodas(int pagina, int tamanio) {
        long offset = (long) pagina * tamanio;
        return findAll()
                .skip(offset)
                .take(tamanio)
                .doOnNext(s -> log.debug("🔍 Solicitud paginada: {}", s.getId()))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error en listado paginado: {}", ex.getMessage());
                    return new RuntimeException("Error en listado paginado", ex);
                });
    }

    @Override
    public Mono<Long> contarPorEstado(EstadoSolicitud estado) {
        String estadoString = estado.name();
        return repository.countByEstado(estadoString)
                .doOnNext(count -> log.debug("📊 Solicitudes en estado {}: {}", estado, count))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error contando por estado {}: {}", estado, ex.getMessage());
                    return new RuntimeException("Error contando solicitudes", ex);
                });
    }

    @Override
    public Flux<Solicitud> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(fechaInicio, fechaFin)
                .map(this::toEntity)
                .doOnNext(s -> log.debug("🔍 Solicitud en rango: {}", s.getId()))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error buscando por rango de fechas: {}", ex.getMessage());
                    return new RuntimeException("Error buscando por fechas", ex);
                });
    }

    @Override
    public Flux<Solicitud> buscarPorTermino(String termino, int pagina, int tamanio) {
        long offset = (long) pagina * tamanio;
        String terminoBusqueda = "%" + termino.toLowerCase() + "%";

        return repository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrNumeroDocumentoContaining(
                        terminoBusqueda, terminoBusqueda, termino)
                .skip(offset)
                .take(tamanio)
                .map(this::toEntity)
                .doOnNext(s -> log.debug("🔍 Solicitud encontrada por término '{}': {}", termino, s.getId()))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error buscando por término '{}': {}", termino, ex.getMessage());
                    return new RuntimeException("Error buscando por término", ex);
                });
    }

    @Override
    public Mono<Void> eliminar(String id) {
        return repository.deleteById(id)
                .doOnSuccess(unused -> log.debug("🗑️ Solicitud eliminada: {}", id))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error eliminando solicitud {}: {}", id, ex.getMessage());
                    return new RuntimeException("Error eliminando solicitud", ex);
                });
    }

    @Override
    public Mono<Solicitud> actualizarEstado(String id, EstadoSolicitud nuevoEstado) {
        return repository.findById(id)
                .doOnNext(entity -> {
                    entity.setEstado(nuevoEstado.name());
                    entity.setFechaActualizacion(LocalDateTime.now());
                })
                .flatMap(repository::save)
                .map(this::toEntity)
                .doOnSuccess(s -> log.debug("🔄 Estado actualizado para solicitud {}: {}", id, nuevoEstado))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error actualizando estado de solicitud {}: {}", id, ex.getMessage());
                    return new RuntimeException("Error actualizando estado", ex);
                });
    }

    @Override
    public Flux<Solicitud> buscarPorEstados(EstadoSolicitud... estados) {
        java.util.List<String> estadosString = java.util.Arrays.stream(estados)
                .map(Enum::name)
                .toList();

        return repository.findByEstadoInOrderByFechaCreacionDesc(estadosString)
                .map(this::toEntity)
                .doOnNext(s -> log.debug("🔍 Solicitud en estados múltiples: {}", s.getId()))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error buscando por múltiples estados: {}", ex.getMessage());
                    return new RuntimeException("Error buscando por estados", ex);
                });
    }

    @Override
    public Mono<Long> contarTodas() {
        return repository.count()
                .doOnNext(count -> log.debug("📊 Total de solicitudes: {}", count))
                .onErrorMap(Exception.class, ex -> {
                    log.error("❌ Error contando todas las solicitudes: {}", ex.getMessage());
                    return new RuntimeException("Error contando solicitudes", ex);
                });
    }
}