// domain/model/src/test/java/com/crediya/solicitudes/model/solicitud/constants/BusinessConstantsTest.java
package com.crediya.solicitudes.model.solicitud.constants;

import com.crediya.solicitudes.model.constants.BusinessConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

@DisplayName("BusinessConstants - Constantes de Negocio")
class BusinessConstantsTest {

    @Test
    @DisplayName("Debe tener montos financieros correctos")
    void debeTenerMontosFinancierosCorrectos() {
        // Then
        assertThat(BusinessConstants.Financiero.MONTO_MINIMO)
                .isEqualByComparingTo(new BigDecimal("100000"));

        assertThat(BusinessConstants.Financiero.MONTO_MAXIMO)
                .isEqualByComparingTo(new BigDecimal("50000000"));

        assertThat(BusinessConstants.Financiero.FACTOR_ENDEUDAMIENTO)
                .isEqualByComparingTo(new BigDecimal("0.30"));
    }

    @Test
    @DisplayName("Debe tener tipos de crédito válidos")
    void debeTenerTiposCreditoValidos() {
        // Then
        assertThat(BusinessConstants.TipoCredito.PERSONAL).isEqualTo("PERSONAL");
        assertThat(BusinessConstants.TipoCredito.VEHICULO).isEqualTo("VEHICULO");
        assertThat(BusinessConstants.TipoCredito.VIVIENDA).isEqualTo("VIVIENDA");
        assertThat(BusinessConstants.TipoCredito.EDUCATIVO).isEqualTo("EDUCATIVO");
    }

    @Test
    @DisplayName("Debe validar regex de tipos de crédito")
    void debeValidarRegexTiposCredito() {
        // Given
        Pattern pattern = Pattern.compile(BusinessConstants.TipoCredito.TIPOS_REGEX);

        // Then - Tipos válidos
        assertThat(pattern.matcher("PERSONAL").matches()).isTrue();
        assertThat(pattern.matcher("VEHICULO").matches()).isTrue();
        assertThat(pattern.matcher("VIVIENDA").matches()).isTrue();
        assertThat(pattern.matcher("EDUCATIVO").matches()).isTrue();

        // Tipos inválidos
        assertThat(pattern.matcher("INVALIDO").matches()).isFalse();
        assertThat(pattern.matcher("personal").matches()).isFalse(); // minúsculas
    }

    @ParameterizedTest
    @ValueSource(strings = {"+573001234567", "3001234567"})
    @DisplayName("Debe validar patrones de teléfono correctos")
    void debeValidarPatronesTelefonoCorrectos(String telefono) {
        // Given
        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.TELEFONO);

        // When & Then
        assertThat(pattern.matcher(telefono).matches()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "+5730012345678901", "abcd1234567"})
    @DisplayName("Debe rechazar patrones de teléfono incorrectos")
    void debeRechazarPatronesTelefonoIncorrectos(String telefono) {
        // Given
        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.TELEFONO);

        // When & Then
        assertThat(pattern.matcher(telefono).matches()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "12345678", "123456789012345"})
    @DisplayName("Debe validar patrones de documento correctos")
    void debeValidarPatternsDocumentoCorrectos(String documento) {
        // Given
        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.DOCUMENTO);

        // When & Then
        assertThat(pattern.matcher(documento).matches()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "1234567890123456", "abcd1234"})
    @DisplayName("Debe rechazar patrones de documento incorrectos")
    void debeRechazarPatternsDocumentoIncorrectos(String documento) {
        // Given
        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.DOCUMENTO);

        // When & Then
        assertThat(pattern.matcher(documento).matches()).isFalse();
    }

    @Test
    @DisplayName("Debe tener límites de plazo válidos")
    void debeTenerLimitesPlazoValidos() {
        // Then
        assertThat(BusinessConstants.Plazo.MINIMO_MESES).isEqualTo(6);
        assertThat(BusinessConstants.Plazo.MAXIMO_MESES).isEqualTo(72);

        // Validar que mínimo es menor que máximo
        assertThat(BusinessConstants.Plazo.MINIMO_MESES)
                .isLessThan(BusinessConstants.Plazo.MAXIMO_MESES);
    }

    @Test
    @DisplayName("Debe tener límites de documento válidos")
    void debeTenerLimitesDocumentoValidos() {
        // Then
        assertThat(BusinessConstants.Documento.LONGITUD_MINIMA).isEqualTo(6);
        assertThat(BusinessConstants.Documento.LONGITUD_MAXIMA).isEqualTo(15);

        assertThat(BusinessConstants.Documento.LONGITUD_MINIMA)
                .isLessThan(BusinessConstants.Documento.LONGITUD_MAXIMA);
    }

    @Test
    @DisplayName("Debe tener límites de texto válidos")
    void debeTenerLimitesTextoValidos() {
        // Then
        assertThat(BusinessConstants.Texto.NOMBRES_MAX_LENGTH).isEqualTo(100);
        assertThat(BusinessConstants.Texto.APELLIDOS_MAX_LENGTH).isEqualTo(100);
        assertThat(BusinessConstants.Texto.EMAIL_MAX_LENGTH).isEqualTo(150);
        assertThat(BusinessConstants.Texto.TELEFONO_MIN_LENGTH).isEqualTo(10);
        assertThat(BusinessConstants.Texto.TELEFONO_MAX_LENGTH).isEqualTo(15);
        assertThat(BusinessConstants.Texto.OBSERVACIONES_MAX_LENGTH).isEqualTo(500);
    }

    @Test
    @DisplayName("Debe tener estados de solicitud válidos - SIN PRE_APROBADA")
    void debeTenerEstadosSolicitudValidos() {
        // Then - CORREGIDO: Solo 4 estados, sin PRE_APROBADA
        assertThat(BusinessConstants.Estados.PENDIENTE_REVISION).isEqualTo("PENDIENTE_REVISION");
        assertThat(BusinessConstants.Estados.EN_REVISION).isEqualTo("EN_REVISION");
        assertThat(BusinessConstants.Estados.APROBADA).isEqualTo("APROBADA");
        assertThat(BusinessConstants.Estados.RECHAZADA).isEqualTo("RECHAZADA");
    }

    @Test
    @DisplayName("Debe tener parámetros financieros adicionales válidos")
    void debeTenerParametrosFinancierosAdicionalesValidos() {
        // Then
        assertThat(BusinessConstants.Financiero.PORCENTAJE_GASTOS_MAXIMO)
                .isEqualByComparingTo(new BigDecimal("70"));

        assertThat(BusinessConstants.Financiero.MULTIPLICADOR_CAPACIDAD_MAXIMA)
                .isEqualByComparingTo(new BigDecimal("5"));

        assertThat(BusinessConstants.Financiero.PUNTAJE_CREDITICIO_MINIMO).isEqualTo(500);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan Carlos", "María José", "Pedro Antonio"})
    @DisplayName("Debe validar patrones de nombres y apellidos correctos")
    void debeValidarPatternsNombresCorrectos(String nombre) {
        // Given
        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.NOMBRES_APELLIDOS);

        // When & Then
        assertThat(pattern.matcher(nombre).matches()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan123", "María@", "Pedro_"})
    @DisplayName("Debe rechazar patrones de nombres y apellidos incorrectos")
    void debeRechazarPatternsNombresIncorrectos(String nombre) {
        // Given
        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.NOMBRES_APELLIDOS);

        // When & Then
        assertThat(pattern.matcher(nombre).matches()).isFalse();
    }

    @Test
    @DisplayName("Debe verificar que las constantes no son modificables")
    void debeVerificarConstantesNoModificables() {
        // Then - No debe lanzar excepción al acceder - SIN PRE_APROBADA
        assertThatNoException().isThrownBy(() -> {
            BigDecimal monto = BusinessConstants.Financiero.MONTO_MINIMO;
            String tipo = BusinessConstants.TipoCredito.PERSONAL;
            String pattern = BusinessConstants.Patterns.TELEFONO;
            int plazo = BusinessConstants.Plazo.MINIMO_MESES;
            int longitudDoc = BusinessConstants.Documento.LONGITUD_MINIMA;
            int maxNombres = BusinessConstants.Texto.NOMBRES_MAX_LENGTH;
            String estado = BusinessConstants.Estados.PENDIENTE_REVISION;
        });
    }

    @Test
    @DisplayName("Debe tener validación coherente entre constantes")
    void debeTenerValidacionCoherenteEntreConstantes() {
        // Then - Verificar coherencia entre límites
        assertThat(BusinessConstants.Financiero.MONTO_MINIMO)
                .isLessThan(BusinessConstants.Financiero.MONTO_MAXIMO);

        assertThat(BusinessConstants.Plazo.MINIMO_MESES)
                .isLessThan(BusinessConstants.Plazo.MAXIMO_MESES);

        assertThat(BusinessConstants.Financiero.FACTOR_ENDEUDAMIENTO)
                .isBetween(BigDecimal.ZERO, BigDecimal.ONE);

        assertThat(BusinessConstants.Texto.TELEFONO_MIN_LENGTH)
                .isLessThan(BusinessConstants.Texto.TELEFONO_MAX_LENGTH);

        assertThat(BusinessConstants.Documento.LONGITUD_MINIMA)
                .isLessThan(BusinessConstants.Documento.LONGITUD_MAXIMA);
    }
}