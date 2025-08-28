// infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/com/crediya/solicitudes/r2dbc/entity/SolicitudEntity.java
package com.crediya.solicitudes.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad R2DBC para la tabla de solicitudes
 * Representación de la estructura de base de datos para R2DBC reactivo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("solicitudes")
public class SolicitudEntity {

    @Id
    @Column("id_solicitud")
    private String id;  // ✅ Para INSERT será null, PostgreSQL generará UUID

    @Column("numero_documento")
    private String numeroDocumento;

    @Column("nombres")
    private String nombres;

    @Column("apellidos")
    private String apellidos;

    @Column("email")
    private String email;

    @Column("telefono")
    private String telefono;

    @Column("monto_solicitado")
    private BigDecimal montoSolicitado;

    @Column("plazo_meses")
    private Integer plazoMeses;

    @Column("tipo_credito")
    private String tipoCredito;

    @Column("ingresos_mensuales")
    private BigDecimal ingresosMensuales;

    @Column("gastos_mensuales")
    private BigDecimal gastosMensuales;

    @Column("estado")
    private String estado;

    @Column("observaciones")
    private String observaciones;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // ========================================
    // MÉTODOS DE CONVENIENCIA
    // ========================================

    /**
     * Método auxiliar para obtener el nombre completo
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /**
     * ✅ NUEVO: Método para determinar si es una nueva entidad
     * R2DBC usa esto para decidir INSERT vs UPDATE
     */
    public boolean isNew() {
        return id == null;
    }

    /**
     * Pre-persist para establecer fecha de creación
     */
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Pre-update para establecer fecha de actualización
     */
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}