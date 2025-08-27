package com.crediya.solicitudes.jpa.adapters;

import com.crediya.solicitudes.model.solicitud.gateways.ValidacionExternaGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

/**
 * Implementación mock del gateway de validaciones externas
 * En un proyecto real, aquí se integraría con:
 * - Bases de datos gubernamentales (RENAPO, CURP, Registraduría Civil)
 * - Centrales de riesgo (TransUnion, Experian, DataCrédito, CIFIN)
 * - Sistemas de consulta financiera (DIAN, PILA, bancos)
 * - APIs de validación de identidad
 * - Servicios de scoring crediticio
 */
@Slf4j
@Component
public class ValidacionExternaGatewayAdapter implements ValidacionExternaGateway {

    private final Random random = new Random();

    @Override
    public Optional<ValidacionDocumento> validarDocumento(String numeroDocumento) {
        try {
            log.info("🔍 VALIDACIÓN DE DOCUMENTO: {}", numeroDocumento);

            // SIMULACIÓN - En un proyecto real aquí iría la integración real
            // con bases de datos oficiales como:
            // - RENAPO (México)
            // - Registraduría Nacional (Colombia)
            // - RENIEC (Perú)
            // - etc.

            // Simulamos diferentes escenarios basados en el documento
            if (numeroDocumento.endsWith("000")) {
                // Documento inválido
                ValidacionDocumento resultado = new ValidacionDocumento(
                        false,
                        "",
                        "",
                        "Documento no encontrado en base de datos oficial"
                );
                log.warn("   ❌ Documento no válido: {}", numeroDocumento);
                return Optional.of(resultado);

            } else if (numeroDocumento.endsWith("999")) {
                // Documento válido pero con discrepancias en nombres
                ValidacionDocumento resultado = new ValidacionDocumento(
                        true,
                        "María José",
                        "García López",
                        "Documento válido - verificar nombres declarados"
                );
                log.warn("   ⚠️  Documento válido con posibles discrepancias en nombres");
                return Optional.of(resultado);

            } else if (numeroDocumento.endsWith("111")) {
                // Documento reportado como fallecido
                ValidacionDocumento resultado = new ValidacionDocumento(
                        false,
                        "Carlos Alberto",
                        "Rodríguez Pérez",
                        "Documento registra persona fallecida"
                );
                log.error("   💀 Documento corresponde a persona fallecida");
                return Optional.of(resultado);

            } else {
                // Documento válido normal
                ValidacionDocumento resultado = new ValidacionDocumento(
                        true,
                        "Juan Carlos",
                        "Pérez Gómez",
                        "Documento válido y verificado en registros oficiales"
                );
                log.info("   ✅ Documento completamente válido");
                return Optional.of(resultado);
            }

        } catch (Exception e) {
            log.error("Error validando documento {}: {}", numeroDocumento, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<HistorialCrediticio> consultarHistorialCrediticio(String numeroDocumento) {
        try {
            log.info("📊 CONSULTA HISTORIAL CREDITICIO: {}", numeroDocumento);

            // SIMULACIÓN - En un proyecto real aquí se consultarían:
            // - TransUnion, Experian, Equifax
            // - DataCrédito, CIFIN (Colombia)
            // - Buró de Crédito (México)
            // - DICOM (Chile)
            // - Centrales de riesgo locales

            // Simulamos diferentes perfiles crediticios basados en el documento
            int score = calcularScoreSimulado(numeroDocumento);
            boolean reportesNegativos = determinarReportesNegativos(score, numeroDocumento);
            int cantidadCreditos = random.nextInt(6); // 0-5 créditos activos
            BigDecimal totalDeudas = calcularDeudasSimuladas(score);

            String observaciones = generarObservacionesCrediticias(score, reportesNegativos, cantidadCreditos);

            HistorialCrediticio historial = new HistorialCrediticio(
                    score,
                    reportesNegativos,
                    cantidadCreditos,
                    totalDeudas,
                    observaciones
            );

            log.info("   📈 Score crediticio: {}/850 ({})", score, clasificarScore(score));
            log.info("   📋 Reportes negativos: {}", reportesNegativos ? "SÍ" : "NO");
            log.info("   💳 Créditos activos: {}", cantidadCreditos);
            log.info("   💰 Deuda total: ${:,.2f}", totalDeudas);
            log.info("   📝 Observación: {}", observaciones);

            return Optional.of(historial);

        } catch (Exception e) {
            log.error("Error consultando historial crediticio para {}: {}",
                    numeroDocumento, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ValidacionFinanciera> validarInformacionFinanciera(String numeroDocumento,
                                                                       BigDecimal ingresosDeclarados) {
        try {
            log.info("💼 VALIDACIÓN FINANCIERA: {} - Ingresos declarados: ${:,.2f}",
                    numeroDocumento, ingresosDeclarados);

            // SIMULACIÓN - En un proyecto real aquí se integraría con:
            // - DIAN (Colombia) - Declaraciones de renta
            // - SAT (México) - Sistemas tributarios
            // - SUNAT (Perú) - Información tributaria
            // - Sistemas de nómina empresariales
            // - APIs bancarias de validación de ingresos
            // - PILA (Colombia) - Planilla Integrada de Liquidación de Aportes

            // Simulamos validación de ingresos basada en patrones del documento
            boolean ingresosVerificados = determinarVerificacionIngresos(numeroDocumento);
            BigDecimal ingresosProbados;
            String fuenteValidacion;
            String observaciones;

            if (ingresosVerificados) {
                // Simulamos variación del ±30% en ingresos reales vs declarados
                double variacion = calcularVariacionIngresos(numeroDocumento);
                ingresosProbados = ingresosDeclarados.multiply(BigDecimal.valueOf(variacion));
                fuenteValidacion = determinarFuenteValidacion(numeroDocumento);
                observaciones = generarObservacionesFinancieras(variacion, ingresosDeclarados, ingresosProbados);

            } else {
                ingresosProbados = BigDecimal.ZERO;
                fuenteValidacion = "No disponible - trabajador informal";
                observaciones = "No se pudieron verificar ingresos en fuentes oficiales. " +
                        "Posible trabajo informal o independiente sin reportes tributarios.";
            }

            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    ingresosVerificados,
                    ingresosProbados,
                    fuenteValidacion,
                    observaciones
            );

            log.info("   {} Verificación: {}",
                    ingresosVerificados ? "✅" : "❌",
                    ingresosVerificados ? "EXITOSA" : "SIN DATOS");
            log.info("   💵 Ingresos comprobados: ${:,.2f}", ingresosProbados);
            log.info("   🏛️  Fuente: {}", fuenteValidacion);
            log.info("   📋 Resultado: {}", observaciones);

            return Optional.of(validacion);

        } catch (Exception e) {
            log.error("Error validando información financiera para {}: {}",
                    numeroDocumento, e.getMessage());
            return Optional.empty();
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES PARA SIMULACIÓN REALISTA
    // ============================================================

    private int calcularScoreSimulado(String numeroDocumento) {
        // Score basado en patrones del documento para consistencia
        int baseScore = Math.abs(numeroDocumento.hashCode()) % 551 + 300; // 300-850

        // Ajustes por patrones específicos
        if (numeroDocumento.contains("888")) {
            return Math.min(800, baseScore + 100); // Buen pagador
        } else if (numeroDocumento.contains("666")) {
            return Math.max(350, baseScore - 150); // Mal pagador
        }

        return baseScore;
    }

    private boolean determinarReportesNegativos(int score, String numeroDocumento) {
        if (score >= 700) {
            return false; // Scores altos raramente tienen reportes negativos
        } else if (score < 500) {
            return true;  // Scores bajos casi siempre tienen reportes negativos
        } else {
            // En el rango medio, depende de otros factores
            return numeroDocumento.contains("5") || numeroDocumento.contains("9");
        }
    }

    private BigDecimal calcularDeudasSimuladas(int score) {
        // Deudas inversamente relacionadas con el score
        int maxDeuda = (850 - score) * 100000; // Peor score = más deuda
        return new BigDecimal(random.nextInt(Math.max(1000000, maxDeuda)));
    }

    private String generarObservacionesCrediticias(int score, boolean reportesNegativos, int cantidadCreditos) {
        StringBuilder obs = new StringBuilder();

        if (score >= 750) {
            obs.append("Excelente historial crediticio. ");
            obs.append("Pagador puntual con alta confiabilidad. ");
        } else if (score >= 650) {
            obs.append("Buen historial crediticio. ");
            obs.append("Algunos retrasos menores sin mayor impacto. ");
        } else if (score >= 550) {
            obs.append("Historial crediticio regular. ");
            obs.append("Requiere análisis detallado de capacidad de pago. ");
        } else {
            obs.append("Historial crediticio deficiente. ");
            obs.append("Múltiples incidencias negativas registradas. ");
        }

        if (reportesNegativos) {
            obs.append("Presenta reportes negativos activos. ");
        }

        if (cantidadCreditos > 3) {
            obs.append("Alto número de créditos simultáneos. ");
        } else if (cantidadCreditos == 0) {
            obs.append("Sin historial crediticio previo. ");
        }

        return obs.toString().trim();
    }

    private boolean determinarVerificacionIngresos(String numeroDocumento) {
        // Simula que ~70% de las personas tienen ingresos verificables
        return Math.abs(numeroDocumento.hashCode()) % 10 < 7;
    }

    private double calcularVariacionIngresos(String numeroDocumento) {
        // Variación entre 0.7 y 1.3 (±30%)
        int hash = Math.abs(numeroDocumento.hashCode());
        return 0.7 + (hash % 60) / 100.0; // 0.7 a 1.29
    }

    private String determinarFuenteValidacion(String numeroDocumento) {
        String[] fuentes = {
                "DIAN - Declaración de renta 2023",
                "PILA - Planilla de aportes",
                "Certificados laborales empresa formal",
                "Estados de cuenta bancarios",
                "Sistema tributario nacional"
        };

        int index = Math.abs(numeroDocumento.hashCode()) % fuentes.length;
        return fuentes[index];
    }

    private String generarObservacionesFinancieras(double variacion, BigDecimal declarados, BigDecimal probados) {
        if (variacion >= 0.95 && variacion <= 1.05) {
            return "Ingresos declarados coinciden con registros oficiales (variación < 5%)";
        } else if (variacion < 0.85) {
            return String.format("Ingresos declarados superiores a registros oficiales. " +
                            "Declarado: $%,.2f, Oficial: $%,.2f (diferencia: %.1f%%)",
                    declarados, probados, (1 - variacion) * 100);
        } else if (variacion > 1.15) {
            return String.format("Ingresos subreportados. Registros oficiales muestran ingresos superiores. " +
                            "Declarado: $%,.2f, Oficial: $%,.2f (diferencia: +%.1f%%)",
                    declarados, probados, (variacion - 1) * 100);
        } else {
            return String.format("Variación normal en ingresos reportados vs oficiales (%.1f%%)",
                    Math.abs(1 - variacion) * 100);
        }
    }

    private String clasificarScore(int score) {
        if (score >= 750) return "EXCELENTE";
        if (score >= 650) return "BUENO";
        if (score >= 550) return "REGULAR";
        if (score >= 450) return "MALO";
        return "MUY MALO";
    }
}
