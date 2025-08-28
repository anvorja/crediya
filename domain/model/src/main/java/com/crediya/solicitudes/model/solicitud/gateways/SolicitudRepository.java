// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/SolicitudRepository.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Gateway para persistencia de solicitudes
 * Define las operaciones de persistencia que necesita el dominio
 */
public interface SolicitudRepository {

    /**
     * Guarda una solicitud (crear o actualizar)
     * @param solicitud la solicitud a guardar
     * @return la solicitud guardada con ID generado
     */
    Solicitud guardar(Solicitud solicitud);

    /**
     * Busca una solicitud por ID único
     * @param id el ID de la solicitud
     * @return la solicitud si existe
     */
    Optional<Solicitud> buscarPorId(String id);

    /**
     * Busca una solicitud por número de documento (máximo una activa)
     * @param numeroDocumento el número de documento del cliente
     * @return la solicitud más reciente para ese documento
     */
    Optional<Solicitud> buscarPorNumeroDocumento(String numeroDocumento);

    /**
     * Busca TODAS las solicitudes asociadas a un documento
     * @param numeroDocumento el número de documento del cliente
     * @return lista de todas las solicitudes históricas para ese documento
     */
    List<Solicitud> buscarPorDocumento(String numeroDocumento);

    /**
     * Verifica si existe una solicitud activa para un documento
     * Una solicitud activa es aquella que está en PENDIENTE_REVISION o EN_REVISION
     * @param numeroDocumento el número de documento
     * @return true si existe una solicitud activa
     */
    boolean existeSolicitudActivaPorDocumento(String numeroDocumento);

    /**
     * Lista solicitudes por estado específico
     * @param estado el estado de la solicitud
     * @return lista de solicitudes en ese estado
     */
    List<Solicitud> buscarPorEstado(EstadoSolicitud estado);

    /**
     * Lista todas las solicitudes sin paginación
     * @return lista de todas las solicitudes
     */
    List<Solicitud> buscarTodas();

    /**
     * Lista todas las solicitudes con paginación
     * @param pagina número de página (base 0)
     * @param tamanio tamaño de la página
     * @return lista paginada de solicitudes
     */
    List<Solicitud> listarTodas(int pagina, int tamanio);

    /**
     * Cuenta solicitudes por estado
     * @param estado el estado a contar
     * @return número de solicitudes en ese estado
     */
    long contarPorEstado(EstadoSolicitud estado);

    /**
     * Lista solicitudes por rango de fechas
     * @param fechaInicio fecha de inicio del rango
     * @param fechaFin fecha final del rango
     * @return lista de solicitudes en ese rango
     */
    List<Solicitud> listarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca solicitudes por término de búsqueda (nombres, apellidos, documento)
     * @param termino término a buscar
     * @param pagina número de página
     * @param tamanio tamaño de página
     * @return lista paginada de solicitudes que coinciden
     */
    List<Solicitud> buscarPorTermino(String termino, int pagina, int tamanio);

    /**
     * Elimina una solicitud por ID
     * @param id el ID de la solicitud a eliminar
     */
    void eliminar(String id);
}