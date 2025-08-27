package com.crediya.solicitudes.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para solicitudes de préstamo
 * Información que se retorna al cliente después de crear/consultar una solicitud
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponse {

    private String id;
    private String numeroDocumento;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private BigDecimal montoSolicitado;
    private String tipoCredito;

    private String estado; // Simplificado: usar string directamente
    private String descripcionEstado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaActualizacion;

    private String observaciones;

    // Información financiera (solo para responses internas/admin)
    private BigDecimal ingresosMensuales;
    private BigDecimal gastosMensuales;

}