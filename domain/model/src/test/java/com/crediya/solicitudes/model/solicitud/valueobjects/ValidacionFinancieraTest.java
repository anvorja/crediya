// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/valueobjects/ValidacionFinancieraTest.java
package com.crediya.solicitudes.model.solicitud.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ValidacionFinanciera - Value Object")
class ValidacionFinancieraTest {

    @Nested
    @DisplayName("Creación del Value Object")
    class CreacionValueObject {

        @Test
        @DisplayName("Debe crear validación financiera exitosa con todos los datos")
        void debeCrearValidacionFinancieraExitosa() {
            // Given
            BigDecimal ingresos = new BigDecimal("3000000");
            String fuente = "DIAN - Declaración de renta 2023";
            String observaciones = "Ingresos verificados correctamente";

            // When
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true, ingresos, fuente, observaciones);

            // Then
            assertThat(validacion.ingresosVerificados()).isTrue();
            assertThat(validacion.ingresosProbados()).isEqualByComparingTo(ingresos);
            assertThat(validacion.fuenteValidacion()).isEqualTo(fuente);
            assertThat(validacion.observaciones()).isEqualTo(observaciones);
            assertThat(validacion.tieneObservaciones()).isTrue();
        }

        @Test
        @DisplayName("Debe crear validación financiera sin verificación")
        void debeCrearValidacionSinVerificacion() {
            // Given
            String fuente = "No disponible - trabajador informal";
            String observaciones = "No se pudieron verificar ingresos en fuentes oficiales";

            // When
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    false, BigDecimal.ZERO, fuente, observaciones);

            // Then
            assertThat(validacion.ingresosVerificados()).isFalse();
            assertThat(validacion.ingresosProbados()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(validacion.fuenteValidacion()).isEqualTo(fuente);
            assertThat(validacion.observaciones()).isEqualTo(observaciones);
            assertThat(validacion.tieneObservaciones()).isTrue();
        }

        @Test
        @DisplayName("Debe manejar observaciones vacías correctamente")
        void debeManejarObservacionesVacias() {
            // Given & When
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("2500000"),
                    "PILA - Planilla de aportes",
                    null  // null se convierte en ""
            );

            // Then
            assertThat(validacion.observaciones()).isEmpty();
            assertThat(validacion.tieneObservaciones()).isFalse();
        }

        @Test
        @DisplayName("Debe manejar observaciones con solo espacios")
        void debeManejarObservacionesConEspacios() {
            // Given & When
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("2500000"),
                    "PILA - Planilla de aportes",
                    "   "  // Solo espacios
            );

            // Then
            assertThat(validacion.observaciones()).isEqualTo("   ");
            assertThat(validacion.tieneObservaciones()).isFalse(); // trim() hace que sea false
        }
    }

    @Nested
    @DisplayName("Validaciones de Entrada")
    class ValidacionesEntrada {

        @Test
        @DisplayName("Debe rechazar ingresos probados negativos")
        void debeRechazarIngresosProbadosNegativos() {
            // Given
            BigDecimal ingresosNegativos = new BigDecimal("-100000");

            // When & Then
            assertThatThrownBy(() -> new ValidacionFinanciera(
                    true,
                    ingresosNegativos,
                    "DIAN",
                    "Test"
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Los ingresos probados no pueden ser negativos");
        }

        @Test
        @DisplayName("Debe rechazar fuente de validación vacía")
        void debeRechazarFuenteValidacionVacia() {
            // When & Then
            assertThatThrownBy(() -> new ValidacionFinanciera(
                    true,
                    new BigDecimal("1000000"),
                    "",
                    "Test"
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("La fuente de validación no puede estar vacía");
        }

        @Test
        @DisplayName("Debe rechazar fuente de validación null")
        void debeRechazarFuenteValidacionNull() {
            // When & Then
            assertThatThrownBy(() -> new ValidacionFinanciera(
                    true,
                    new BigDecimal("1000000"),
                    null,
                    "Test"
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("La fuente de validación no puede estar vacía");
        }

        @Test
        @DisplayName("Debe rechazar fuente de validación con solo espacios")
        void debeRechazarFuenteValidacionSoloEspacios() {
            // When & Then
            assertThatThrownBy(() -> new ValidacionFinanciera(
                    true,
                    new BigDecimal("1000000"),
                    "   ",
                    "Test"
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("La fuente de validación no puede estar vacía");
        }

        @Test
        @DisplayName("Debe convertir ingresos probados null a ZERO")
        void debeConvertirIngresosProbadosNullACero() {
            // When
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    false,
                    null,  // Se debe convertir a ZERO
                    "No disponible",
                    "Sin ingresos"
            );

            // Then
            assertThat(validacion.ingresosProbados()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Cálculo de Variación Porcentual")
    class CalculoVariacionPorcentual {

        @Test
        @DisplayName("Debe calcular variación cuando ingresos probados son mayores")
        void debeCalcularVariacionIngresosProbadomMayores() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("3600000"), // Probados: 3.6M
                    "DIAN",
                    "Test"
            );
            BigDecimal declarados = new BigDecimal("3000000"); // Declarados: 3M

            // When
            double variacion = validacion.calcularVariacionPorcentaje(declarados);

            // Then
            assertThat(variacion).isEqualTo(20.0); // (3.6M - 3M) / 3M * 100 = 20%
        }

        @Test
        @DisplayName("Debe calcular variación cuando ingresos probados son menores")
        void debeCalcularVariacionIngresosProbadomMenores() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("2400000"), // Probados: 2.4M
                    "DIAN",
                    "Test"
            );
            BigDecimal declarados = new BigDecimal("3000000"); // Declarados: 3M

            // When
            double variacion = validacion.calcularVariacionPorcentaje(declarados);

            // Then
            assertThat(variacion).isEqualTo(-20.0); // (2.4M - 3M) / 3M * 100 = -20%
        }

        @Test
        @DisplayName("Debe retornar 0 cuando ingresos declarados son cero")
        void debeRetornarCeroCuandoIngresosDeclaradosCero() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("1000000"),
                    "DIAN",
                    "Test"
            );

            // When
            double variacion = validacion.calcularVariacionPorcentaje(BigDecimal.ZERO);

            // Then
            assertThat(variacion).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Debe retornar 0 cuando ingresos declarados son null")
        void debeRetornarCeroCuandoIngresosDeclaradosNull() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("1000000"),
                    "DIAN",
                    "Test"
            );

            // When
            double variacion = validacion.calcularVariacionPorcentaje(null);

            // Then
            assertThat(variacion).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Debe retornar 0 cuando ingresos no están verificados")
        void debeRetornarCeroCuandoIngresosNoVerificados() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    false, // No verificados
                    new BigDecimal("1000000"),
                    "No disponible",
                    "Sin verificación"
            );

            // When
            double variacion = validacion.calcularVariacionPorcentaje(new BigDecimal("2000000"));

            // Then
            assertThat(variacion).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Debe retornar 0 cuando ingresos probados son cero")
        void debeRetornarCeroCuandoIngresosProbadomCero() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    BigDecimal.ZERO, // Probados = 0
                    "DIAN",
                    "Sin ingresos registrados"
            );

            // When
            double variacion = validacion.calcularVariacionPorcentaje(new BigDecimal("1000000"));

            // Then
            assertThat(variacion).isEqualTo(0.0);
        }

        @ParameterizedTest
        @MethodSource("proveerCasosVariacion")
        @DisplayName("Debe calcular variaciones correctamente para múltiples casos")
        void debeCalcularVariacionesMultiplesCasos(
                BigDecimal declarados,
                BigDecimal probados,
                double variacionEsperada) {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true, probados, "DIAN", "Test"
            );

            // When
            double variacion = validacion.calcularVariacionPorcentaje(declarados);

            // Then
            assertThat(variacion).isCloseTo(variacionEsperada, within(0.01));
        }

        private static Stream<Arguments> proveerCasosVariacion() {
            return Stream.of(
                    Arguments.of(new BigDecimal("1000000"), new BigDecimal("1000000"), 0.0),    // Sin variación
                    Arguments.of(new BigDecimal("1000000"), new BigDecimal("1100000"), 10.0),   // +10%
                    Arguments.of(new BigDecimal("1000000"), new BigDecimal("900000"), -10.0),   // -10%
                    Arguments.of(new BigDecimal("2000000"), new BigDecimal("2500000"), 25.0),   // +25%
                    Arguments.of(new BigDecimal("5000000"), new BigDecimal("4000000"), -20.0)   // -20%
            );
        }
    }

    @Nested
    @DisplayName("Validación de Variación Aceptable")
    class ValidacionVariacionAceptable {

        @ParameterizedTest
        @ValueSource(doubles = {0.0, 5.0, 10.0, 15.0, 20.0, -5.0, -10.0, -15.0, -20.0})
        @DisplayName("Debe considerar aceptables variaciones menores o iguales a 20%")
        void debeConsiderarAceptablesVariacionesMenoresA20Porciento(double porcentaje) {
            // Given
            BigDecimal declarados = new BigDecimal("1000000");
            BigDecimal probados = declarados.multiply(
                    BigDecimal.valueOf(1 + porcentaje / 100.0)
            );

            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true, probados, "DIAN", "Test"
            );

            // When
            boolean esAceptable = validacion.esVariacionAceptable(declarados);

            // Then
            assertThat(esAceptable).isTrue();
        }

        @ParameterizedTest
        @ValueSource(doubles = {25.0, 30.0, 50.0, -25.0, -30.0, -50.0})
        @DisplayName("Debe considerar inaceptables variaciones mayores a 20%")
        void debeConsiderarInaceptablesVariacionesMayoresA20Porciento(double porcentaje) {
            // Given
            BigDecimal declarados = new BigDecimal("1000000");
            BigDecimal probados = declarados.multiply(
                    BigDecimal.valueOf(1 + porcentaje / 100.0)
            );

            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true, probados, "DIAN", "Test"
            );

            // When
            boolean esAceptable = validacion.esVariacionAceptable(declarados);

            // Then
            assertThat(esAceptable).isFalse();
        }

        @Test
        @DisplayName("Debe considerar aceptable cuando ingresos no están verificados")
        void debeConsiderarAceptableCuandoIngresosNoVerificados() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    false, // No verificados
                    BigDecimal.ZERO,
                    "No disponible",
                    "Sin verificación"
            );

            // When
            boolean esAceptable = validacion.esVariacionAceptable(new BigDecimal("1000000"));

            // Then
            assertThat(esAceptable).isTrue(); // Sin verificación = no hay conflicto
        }
    }

    @Nested
    @DisplayName("Casos de Uso Reales")
    class CasosUsoReales {

        @Test
        @DisplayName("Escenario: Trabajador formal con ingresos verificados en DIAN")
        void escenarioTrabajadorFormalDIAN() {
            // Given - Trabajador que declara $3M pero en DIAN aparece $3.2M
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("3200000"), // Probados en DIAN
                    "DIAN - Declaración de renta 2023",
                    "Ingresos verificados. Variación normal del 6.7%"
            );
            BigDecimal declarados = new BigDecimal("3000000");

            // When & Then
            assertThat(validacion.ingresosVerificados()).isTrue();
            assertThat(validacion.calcularVariacionPorcentaje(declarados)).isCloseTo(6.67, within(0.01));
            assertThat(validacion.esVariacionAceptable(declarados)).isTrue();
            assertThat(validacion.tieneObservaciones()).isTrue();
        }

        @Test
        @DisplayName("Escenario: Trabajador informal sin verificación")
        void escenarioTrabajadorInformal() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    false,
                    BigDecimal.ZERO,
                    "No disponible - trabajador informal",
                    "No se pudieron verificar ingresos en fuentes oficiales. " +
                            "Posible trabajo informal o independiente sin reportes tributarios."
            );

            // When & Then
            assertThat(validacion.ingresosVerificados()).isFalse();
            assertThat(validacion.ingresosProbados()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(validacion.fuenteValidacion()).contains("informal");
            assertThat(validacion.tieneObservaciones()).isTrue();
            // Para trabajador informal, cualquier declaración se considera "aceptable"
            assertThat(validacion.esVariacionAceptable(new BigDecimal("2000000"))).isTrue();
        }

        @Test
        @DisplayName("Escenario: Discrepancia mayor - posible fraude")
        void escenarioDiscrepanciaMayor() {
            // Given - Cliente declara $5M pero en registros oficiales solo $2M
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("2000000"), // Probados oficiales
                    "PILA - Planilla de aportes",
                    "ALERTA: Ingresos declarados superiores en 150% a registros oficiales. " +
                            "Requiere documentación adicional."
            );
            BigDecimal declarados = new BigDecimal("5000000");

            // When & Then
            assertThat(validacion.calcularVariacionPorcentaje(declarados)).isCloseTo(-60.0, within(0.01));
            assertThat(validacion.esVariacionAceptable(declarados)).isFalse();
            assertThat(validacion.observaciones()).contains("ALERTA");
        }
    }

    @Nested
    @DisplayName("Comparación y Igualdad")
    class ComparacionIgualdad {

        @Test
        @DisplayName("Debe comparar validaciones financieras por igualdad")
        void debeCompararValidacionesPorIgualdad() {
            // Given
            BigDecimal ingresos = new BigDecimal("3000000");
            String fuente = "DIAN";
            String observaciones = "Test";

            ValidacionFinanciera validacion1 = new ValidacionFinanciera(
                    true, ingresos, fuente, observaciones);
            ValidacionFinanciera validacion2 = new ValidacionFinanciera(
                    true, ingresos, fuente, observaciones);
            ValidacionFinanciera validacion3 = new ValidacionFinanciera(
                    false, ingresos, fuente, observaciones);

            // Then
            assertThat(validacion1).isEqualTo(validacion2);
            assertThat(validacion1).isNotEqualTo(validacion3);
            assertThat(validacion1.hashCode()).isEqualTo(validacion2.hashCode());
        }

        @Test
        @DisplayName("Debe tener toString informativo")
        void debeTenerToStringInformativo() {
            // Given
            ValidacionFinanciera validacion = new ValidacionFinanciera(
                    true,
                    new BigDecimal("2500000"),
                    "DIAN - Declaración de renta 2023",
                    "Ingresos verificados correctamente"
            );

            // When
            String toString = validacion.toString();

            // Then
            assertThat(toString).contains("ValidacionFinanciera");
            assertThat(toString).contains("true");
            assertThat(toString).contains("2500000");
            assertThat(toString).contains("DIAN");
        }
    }
}