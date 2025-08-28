// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/gateways/ValidacionExternaGateway.java
package com.crediya.solicitudes.model.solicitud.gateways;

import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionDocumento;
import com.crediya.solicitudes.model.solicitud.valueobjects.HistorialCrediticio;
import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionFinanciera;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Gateway REACTIVO para validaciones externas con entidades gubernamentales,
 * centrales de riesgo y sistemas financieros
 */
public interface ValidacionExternaGateway {

    /**
     * Valida un documento de identidad contra registros oficiales de forma reactiva
     * (RENAPO, Registraduría Civil, RENIEC, etc.)
     *
     * @param numeroDocumento el número de documento a validar
     * @return Mono<ValidacionDocumento> con el resultado de la validación, Mono.empty() si no hay datos
     */
    Mono<ValidacionDocumento> validarDocumento(String numeroDocumento);

    /**
     * Consulta el historial crediticio en centrales de riesgo de forma reactiva
     * (TransUnion, Experian, DataCrédito, CIFIN, Buró de Crédito, etc.)
     *
     * @param numeroDocumento el número de documento del cliente
     * @return Mono<HistorialCrediticio> con el historial crediticio completo, Mono.empty() si no está disponible
     */
    Mono<HistorialCrediticio> consultarHistorialCrediticio(String numeroDocumento);

    /**
     * Valida información financiera contra fuentes oficiales de forma reactiva
     * (DIAN, SAT, SUNAT, PILA, sistemas tributarios, etc.)
     *
     * @param numeroDocumento el número de documento del cliente
     * @param ingresosDeclarados ingresos declarados por el cliente
     * @return Mono<ValidacionFinanciera> con la validación financiera, Mono.empty() si no hay datos
     */
    Mono<ValidacionFinanciera> validarInformacionFinanciera(String numeroDocumento,
                                                            BigDecimal ingresosDeclarados);

    /**
     * Verifica si una persona está en listas restrictivas de forma reactiva
     * (OFAC, listas de lavado de activos, etc.)
     *
     * @param numeroDocumento el número de documento a verificar
     * @return Mono<Boolean> true si está en listas restrictivas, false en caso contrario
     */
    default Mono<Boolean> verificarListasRestrictivas(String numeroDocumento) {
        // Implementación por defecto - los adapters pueden sobrescribirla
        return Mono.just(false);
    }

    /**
     * Consulta ingresos reportados en fuentes externas de forma reactiva
     * (sistemas de nómina, declaraciones tributarias, etc.)
     *
     * @param numeroDocumento el número de documento del cliente
     * @return Mono<BigDecimal> con los ingresos oficiales reportados, Mono.empty() si no están disponibles
     */
    default Mono<BigDecimal> consultarIngresosDeclarados(String numeroDocumento) {
        // Implementación por defecto - los adapters pueden sobrescribirla
        return Mono.empty();
    }
}