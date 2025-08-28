// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/ValidacionExternaGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionDocumento;
import com.crediya.solicitudes.model.solicitud.valueobjects.HistorialCrediticio;
import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionFinanciera;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Gateway para validaciones externas con entidades gubernamentales,
 * centrales de riesgo y sistemas financieros
 */
public interface ValidacionExternaGateway {

    /**
     * Valida un documento de identidad contra registros oficiales
     * (RENAPO, Registraduría Civil, RENIEC, etc.)
     *
     * @param numeroDocumento el número de documento a validar
     * @return resultado de la validación con datos oficiales
     */
    Optional<ValidacionDocumento> validarDocumento(String numeroDocumento);

    /**
     * Consulta el historial crediticio en centrales de riesgo
     * (TransUnion, Experian, DataCrédito, CIFIN, Buró de Crédito, etc.)
     *
     * @param numeroDocumento el número de documento del cliente
     * @return historial crediticio completo si está disponible
     */
    Optional<HistorialCrediticio> consultarHistorialCrediticio(String numeroDocumento);

    /**
     * Valida información financiera contra fuentes oficiales
     * (DIAN, SAT, SUNAT, PILA, sistemas tributarios, etc.)
     *
     * @param numeroDocumento el número de documento del cliente
     * @param ingresosDeclarados ingresos declarados por el cliente
     * @return validación financiera con ingresos verificados
     */
    Optional<ValidacionFinanciera> validarInformacionFinanciera(String numeroDocumento,
                                                                BigDecimal ingresosDeclarados);

    /**
     * Verifica si una persona está en listas restrictivas
     * (OFAC, listas de lavado de activos, etc.)
     *
     * @param numeroDocumento el número de documento a verificar
     * @return true si está en listas restrictivas
     */
    default boolean verificarListasRestrictivas(String numeroDocumento) {
        // Implementación por defecto - los adapters pueden sobrescribirla
        return false;
    }

    /**
     * Consulta ingresos reportados en fuentes externas
     * (sistemas de nómina, declaraciones tributarias, etc.)
     *
     * @param numeroDocumento el número de documento del cliente
     * @return ingresos oficiales reportados, si están disponibles
     */
    default Optional<BigDecimal> consultarIngresosDeclarados(String numeroDocumento) {
        // Implementación por defecto - los adapters pueden sobrescribirla
        return Optional.empty();
    }
}