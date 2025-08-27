// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/SolicitudRepository.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;

import java.util.List;
import java.util.Optional;

/**
 * Gateway para persistencia de solicitudes
 */
public interface SolicitudRepository {

    /**
     * Guarda una solicitud
     * @param solicitud la solicitud a guardar
     * @return la solicitud guardada
     */
    Solicitud guardar(Solicitud solicitud);

    /**
     * Busca una solicitud por ID
     * @param id el ID de la solicitud
     * @return la solicitud si existe
     */
    Optional<Solicitud> buscarPorId(String id);

    /**
     * Busca solicitudes por número de documento
     * @param numeroDocumento el número de documento
     * @return lista de solicitudes
     */
    List<Solicitud> buscarPorDocumento(String numeroDocumento);

    /**
     * Verifica si existe una solicitud activa para un documento
     * @param numeroDocumento el número de documento
     * @return true si existe una solicitud activa
     */
    boolean existeSolicitudActivaPorDocumento(String numeroDocumento);

    /**
     * Busca solicitudes por estado
     * @param estado el estado de la solicitud
     * @return lista de solicitudes
     */
    List<Solicitud> buscarPorEstado(EstadoSolicitud estado);

    /**
     * Busca todas las solicitudes
     * @return lista de todas las solicitudes
     */
    List<Solicitud> buscarTodas();

    /**
     * Elimina una solicitud por ID
     * @param id el ID de la solicitud
     */
    void eliminar(String id);
}