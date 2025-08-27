package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Gateway para la persistencia de solicitudes (Puerto Secundario)
 */
public interface SolicitudRepository {

    /**
     * Guarda una nueva solicitud o actualiza una existente
     */
    Solicitud guardar(Solicitud solicitud);

    /**
     * Busca una solicitud por su ID
     */
    Optional<Solicitud> buscarPorId(String id);

    /**
     * Busca una solicitud por número de documento
     */
    Optional<Solicitud> buscarPorNumeroDocumento(String numeroDocumento);

    /**
     * Lista todas las solicitudes por estado
     */
    List<Solicitud> listarPorEstado(EstadoSolicitud estado);

    /**
     * Lista todas las solicitudes paginadas
     */
    List<Solicitud> listarTodas(int pagina, int tamanio);

    /**
     * Verifica si existe una solicitud activa para un número de documento
     */
    boolean existeSolicitudActivaPorDocumento(String numeroDocumento);

    /**
     * Cuenta el total de solicitudes por estado
     */
    long contarPorEstado(EstadoSolicitud estado);

    /**
     * Lista solicitudes por rango de fechas
     */
    List<Solicitud> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca solicitudes por términos de búsqueda
     */
    List<Solicitud> buscarPorTermino(String termino, int pagina, int tamanio);
}