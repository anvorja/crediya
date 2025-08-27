// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/ValidacionExternaGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Gateway para validaciones externas (Puerto Secundario)
 */
public interface ValidacionExternaGateway {

    /**
     * Valida si un número de documento existe en bases de datos oficiales
     */
    Optional<ValidacionDocumento> validarDocumento(String numeroDocumento);

    /**
     * Consulta el historial crediticio del solicitante
     */
    Optional<HistorialCrediticio> consultarHistorialCrediticio(String numeroDocumento);

    /**
     * Valida la información financiera del solicitante
     */
    Optional<ValidacionFinanciera> validarInformacionFinanciera(String numeroDocumento, BigDecimal ingresosDeclarados);

    // Value Objects para las respuestas - DOMINIO PURO
    record ValidacionDocumento(
            boolean esValido,
            String nombre,
            String apellido,
            String mensaje
    ) {}

    record HistorialCrediticio(
            int puntajeCrediticio,
            boolean tieneReportesNegativos,
            int cantidadCreditos,
            BigDecimal totalDeudas,
            String observaciones
    ) {}

    record ValidacionFinanciera(
            boolean ingresosVerificados,
            BigDecimal ingresosProbados,
            String fuenteValidacion,
            String observaciones
    ) {}
}