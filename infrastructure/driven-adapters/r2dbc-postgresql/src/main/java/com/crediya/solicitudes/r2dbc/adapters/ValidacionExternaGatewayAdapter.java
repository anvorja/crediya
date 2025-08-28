// infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/com/crediya/solicitudes/r2dbc/adapters/ValidacionExternaGatewayAdapter.java
package com.crediya.solicitudes.r2dbc.adapters;

import com.crediya.solicitudes.model.solicitud.gateways.ValidacionExternaGateway;
import com.crediya.solicitudes.model.solicitud.valueobjects.HistorialCrediticio;
import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionDocumento;
import com.crediya.solicitudes.model.solicitud.valueobjects.ValidacionFinanciera;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Implementación MOCK reactiva del ValidacionExternaGateway
 * En producción se reemplazaría por integración real con servicios externos
 */
@Slf4j
@Component
public class ValidacionExternaGatewayAdapter implements ValidacionExternaGateway {

    @Override
    public Mono<ValidacionDocumento> validarDocumento(String numeroDocumento) {
        return Mono.fromCallable(() -> {
                    log.info("🔍 VALIDACIÓN DOCUMENTO (MOCK): {}", numeroDocumento);

                    // Simular lógica de validación
                    boolean esValido = numeroDocumento.length() >= 8 && numeroDocumento.matches("\\d+");
                    String nombre = "Juan";
                    String apellido = "Pérez";

                    log.info("   • Resultado: {} - {} {}", esValido ? "VÁLIDO" : "INVÁLIDO", nombre, apellido);

                    return new ValidacionDocumento(esValido, nombre, apellido);
                })
                .doOnSuccess(validacion -> log.debug("✅ Validación documento completada"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error en validación documento: {}", ex.getMessage());
                    // Retornar validación exitosa por defecto en caso de error
                    return Mono.just(new ValidacionDocumento(true, "N/A", "N/A"));
                });
    }

    @Override
    public Mono<HistorialCrediticio> consultarHistorialCrediticio(String numeroDocumento) {
        return Mono.fromCallable(() -> {
                    log.info("📊 CONSULTA HISTORIAL CREDITICIO (MOCK): {}", numeroDocumento);

                    // Simular historial crediticio
                    boolean tieneReportes = false;
                    int puntaje = 650; // Puntaje simulado

                    // ✅ CORREGIDO: Usar la variable tieneReportes (ya no es redundante)
                    log.info("   • Reportes negativos: {} - Puntaje: {}",
                            tieneReportes ? "SÍ" : "NO", puntaje);

                    return new HistorialCrediticio(tieneReportes, puntaje);
                })
                .doOnSuccess(historial -> log.debug("✅ Consulta historial completada"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error en consulta historial: {}", ex.getMessage());
                    // Retornar historial limpio por defecto en caso de error
                    return Mono.just(new HistorialCrediticio(false, 600));
                });
    }

    @Override
    public Mono<ValidacionFinanciera> validarInformacionFinanciera(String numeroDocumento, BigDecimal ingresosDeclarados) {
        return Mono.fromCallable(() -> {
                    log.info("💰 VALIDACIÓN FINANCIERA (MOCK): {} - Ingresos: ${:,.2f}",
                            numeroDocumento, ingresosDeclarados);

                    // Simular validación financiera
                    boolean ingresosVerificados = ingresosDeclarados.compareTo(BigDecimal.ZERO) > 0;

                    // ✅ CORREGIDO: Constructor con 4 parámetros
                    String fuenteValidacion = "MOCK - Simulación Externa";
                    String observaciones = ingresosVerificados ?
                            "Ingresos verificados correctamente" :
                            "Ingresos no pueden ser cero o negativos";

                    log.info("   • Resultado: {} - Ingresos verificados: ${:,.2f}",
                            ingresosVerificados ? "VÁLIDO" : "INVÁLIDO", ingresosDeclarados);

                    // ✅ CORREGIDO: Usar los 4 parámetros requeridos por el constructor
                    return new ValidacionFinanciera(
                            ingresosVerificados,      // boolean ingresosVerificados
                            ingresosDeclarados,       // BigDecimal ingresosProbados
                            fuenteValidacion,         // String fuenteValidacion
                            observaciones             // String observaciones
                    );
                })
                .doOnSuccess(validacion -> log.debug("✅ Validación financiera completada"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error en validación financiera: {}", ex.getMessage());
                    // Retornar validación exitosa por defecto
                    return Mono.just(new ValidacionFinanciera(
                            true,
                            ingresosDeclarados,
                            "MOCK - Error Recovery",
                            "Validación por defecto debido a error"
                    ));
                });
    }

    @Override
    public Mono<Boolean> verificarListasRestrictivas(String numeroDocumento) {
        return Mono.fromCallable(() -> {
                    log.info("🚨 VERIFICACIÓN LISTAS RESTRICTIVAS (MOCK): {}", numeroDocumento);

                    // Por defecto, no está en listas restrictivas
                    boolean estaEnListas = false;

                    log.info("   • Resultado: {}", estaEnListas ? "EN LISTAS" : "LIMPIO");
                    return estaEnListas;
                })
                .doOnSuccess(resultado -> log.debug("✅ Verificación listas completada"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error en verificación listas: {}", ex.getMessage());
                    return Mono.just(false); // Por defecto, no está en listas
                });
    }

    @Override
    public Mono<BigDecimal> consultarIngresosDeclarados(String numeroDocumento) {
        return Mono.fromCallable(() -> {
                    log.info("💼 CONSULTA INGRESOS DECLARADOS (MOCK): {}", numeroDocumento);

                    // Simular ingresos declarados
                    BigDecimal ingresos = new BigDecimal("3000000"); // 3 millones simulados

                    log.info("   • Ingresos encontrados: ${:,.2f}", ingresos);
                    return ingresos;
                })
                .doOnSuccess(ingresos -> log.debug("✅ Consulta ingresos completada"))
                .onErrorResume(ex -> {
                    log.warn("⚠️ Error en consulta ingresos: {}", ex.getMessage());
                    return Mono.empty(); // No hay datos disponibles
                });
    }
}