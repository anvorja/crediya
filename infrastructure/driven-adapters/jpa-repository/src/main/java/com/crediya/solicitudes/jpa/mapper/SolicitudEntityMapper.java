package com.crediya.solicitudes.jpa.mapper;

import com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para conversión entre Solicitud (dominio) y SolicitudEntity (JPA)
 */
@Mapper
public interface SolicitudEntityMapper {

    SolicitudEntityMapper INSTANCE = Mappers.getMapper(SolicitudEntityMapper.class);

    /**
     * Convierte de entidad del dominio a entidad JPA
     */
    @Mapping(source = "estado", target = "estado")
    SolicitudEntity toEntity(Solicitud solicitud);

    /**
     * Convierte de entidad JPA a entidad del dominio
     */
    @Mapping(source = "estado", target = "estado")
    Solicitud toDomain(SolicitudEntity entity);

    /**
     * Mapeo de estados del dominio a JPA
     */
    @ValueMapping(source = "PENDIENTE_REVISION", target = "PENDIENTE_REVISION")
    @ValueMapping(source = "PRE_APROBADA", target = "PRE_APROBADA")
    @ValueMapping(source = "APROBADA", target = "APROBADA")
    @ValueMapping(source = "RECHAZADA", target = "RECHAZADA")
    SolicitudEntity.EstadoSolicitudEntity mapEstadoToEntity(EstadoSolicitud estado);

    /**
     * Mapeo de estados de JPA a dominio
     */
    @ValueMapping(source = "PENDIENTE_REVISION", target = "PENDIENTE_REVISION")
    @ValueMapping(source = "PRE_APROBADA", target = "PRE_APROBADA")
    @ValueMapping(source = "APROBADA", target = "APROBADA")
    @ValueMapping(source = "RECHAZADA", target = "RECHAZADA")
    EstadoSolicitud mapEstadoToDomain(SolicitudEntity.EstadoSolicitudEntity estado);
}