package com.crediya.solicitudes.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes", indexes = {
        @Index(name = "idx_numero_documento", columnList = "numero_documento", unique = true),
        @Index(name = "idx_estado", columnList = "estado"),
        @Index(name = "idx_fecha_creacion", columnList = "fecha_creacion")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "numero_documento", nullable = false, length = 15)
    private String numeroDocumento;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(name = "monto_solicitado", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoSolicitado;

    @Column(name = "tipo_credito", nullable = false, length = 20)
    private String tipoCredito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitudEntity estado;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "ingresos_mensuales", precision = 15, scale = 2)
    private BigDecimal ingresosMensuales;

    @Column(name = "gastos_mensuales", precision = 15, scale = 2)
    private BigDecimal gastosMensuales;

    public enum EstadoSolicitudEntity {
        PENDIENTE_REVISION,
        EN_REVISION,
        APROBADA,
        RECHAZADA
    }
}