package com.crediya.solicitudes.api.mapper;

import com.crediya.solicitudes.api.dto.request.CrearSolicitudRequest;
import com.crediya.solicitudes.api.dto.response.SolicitudResponse;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversión entre DTOs REST y entidades de dominio
 * Siguiendo el patrón simple de MapStruct
 */
@Mapper(componentModel = "spring")
public interface SolicitudRestMapper {

    /**
     * Convierte DTO Request a entidad de dominio
     */
    Solicitud toModel(CrearSolicitudRequest request);

    /**
     * Convierte entidad de dominio a DTO Response
     */
    @Mapping(target = "nombreCompleto", expression = "java(solicitud.getNombres() + \" \" + solicitud.getApellidos())")
    @Mapping(target = "estado", expression = "java(solicitud.getEstado().name())")
    @Mapping(target = "descripcionEstado", expression = "java(solicitud.getEstado().getDescripcion())")
    SolicitudResponse toResponse(Solicitud solicitud);

}