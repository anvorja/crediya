// infrastructure/driven-adapters/jpa-repository/src/main/java/com/crediya/solicitudes/jpa/mapper/SolicitudEntityMapper.java
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
     * MapStruct mapea automáticamente los campos con el mismo nombre
     */
    @Mapping(source = "estado", target = "estado")
    SolicitudEntity toEntity(Solicitud solicitud);

    /**
     * Convierte de entidad JPA a entidad del dominio
     * MapStruct mapea automáticamente los campos con el mismo nombre
     */
    @Mapping(source = "estado", target = "estado")
    Solicitud toDomain(SolicitudEntity entity);

    /**
     * Mapeo explícito de estados del dominio a JPA
     * Garantiza que los enums estén perfectamente sincronizados
     */
    @ValueMapping(source = "PENDIENTE_REVISION", target = "PENDIENTE_REVISION")
    @ValueMapping(source = "EN_REVISION", target = "EN_REVISION")
    @ValueMapping(source = "APROBADA", target = "APROBADA")
    @ValueMapping(source = "RECHAZADA", target = "RECHAZADA")
    SolicitudEntity.EstadoSolicitudEntity mapEstadoToEntity(EstadoSolicitud estado);

    /**
     * Mapeo explícito de estados de JPA a dominio
     * Garantiza que los enums estén perfectamente sincronizados
     */
    @ValueMapping(source = "PENDIENTE_REVISION", target = "PENDIENTE_REVISION")
    @ValueMapping(source = "EN_REVISION", target = "EN_REVISION")
    @ValueMapping(source = "APROBADA", target = "APROBADA")
    @ValueMapping(source = "RECHAZADA", target = "RECHAZADA")
    EstadoSolicitud mapEstadoToDomain(SolicitudEntity.EstadoSolicitudEntity estado);

    /**
     * Método de conveniencia para mapear listas de entidades JPA a dominio
     * Útil para operaciones de consulta masiva
     */
    default java.util.List<Solicitud> toDomainList(java.util.List<SolicitudEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Método de conveniencia para mapear listas de entidades de dominio a JPA
     * Útil para operaciones de guardado masivo
     */
    default java.util.List<SolicitudEntity> toEntityList(java.util.List<Solicitud> solicitudes) {
        if (solicitudes == null) {
            return null;
        }

        return solicitudes.stream()
                .map(this::toEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}