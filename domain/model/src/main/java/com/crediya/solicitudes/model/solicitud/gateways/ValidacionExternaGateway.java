// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/ValidacionExternaGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionDocumento;
import com.crediya.solicitudes.model.solicitud.valueobjects.HistorialCrediticio;

import java.util.Optional;

/**
 * Gateway para validaciones externas
 */
public interface ValidacionExternaGateway {

    /**
     * Valida un documento de identidad
     * @param numeroDocumento el número de documento
     * @return resultado de la validación si está disponible
     */
    Optional<ValidacionDocumento> validarDocumento(String numeroDocumento);

    /**
     * Consulta el historial crediticio de una persona
     * @param numeroDocumento el número de documento
     * @return historial crediticio si está disponible
     */
    Optional<HistorialCrediticio> consultarHistorialCrediticio(String numeroDocumento);

    /**
     * Verifica si una persona está en listas restrictivas
     * @param numeroDocumento el número de documento
     * @return true si está en listas restrictivas
     */
    boolean verificarListasRestrictivas(String numeroDocumento);

    /**
     * Consulta ingresos declarados en fuentes externas
     * @param numeroDocumento el número de documento
     * @return ingresos declarados, si están disponibles
     */
    Optional<java.math.BigDecimal> consultarIngresosDeclarados(String numeroDocumento);
}