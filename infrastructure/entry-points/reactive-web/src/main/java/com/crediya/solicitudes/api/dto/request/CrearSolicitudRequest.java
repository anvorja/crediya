package com.crediya.solicitudes.api.dto.request;

import com.crediya.solicitudes.model.constants.BusinessConstants;
import com.crediya.solicitudes.api.constants.ValidationMessagesConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la creación de solicitudes de préstamo
 * Validaciones de entrada según requisitos de negocio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearSolicitudRequest {

    @NotBlank(message = ValidationMessagesConstants.Validation.DOCUMENTO_REQUERIDO)
    @Size(min = BusinessConstants.Documento.LONGITUD_MINIMA,
            max = BusinessConstants.Documento.LONGITUD_MAXIMA,
            message = ValidationMessagesConstants.Validation.DOCUMENTO_LONGITUD)
    @Pattern(regexp = BusinessConstants.Patterns.SOLO_NUMEROS,
            message = ValidationMessagesConstants.Validation.DOCUMENTO_FORMATO)
    private String numeroDocumento;

    @NotBlank(message = ValidationMessagesConstants.Validation.NOMBRES_REQUERIDOS)
    @Size(max = BusinessConstants.Texto.NOMBRES_MAX_LENGTH,
            message = ValidationMessagesConstants.Validation.NOMBRES_LONGITUD)
    @Pattern(regexp = BusinessConstants.Patterns.NOMBRES_APELLIDOS,
            message = ValidationMessagesConstants.Validation.NOMBRES_FORMATO)
    private String nombres;

    @NotBlank(message = ValidationMessagesConstants.Validation.APELLIDOS_REQUERIDOS)
    @Size(max = BusinessConstants.Texto.APELLIDOS_MAX_LENGTH,
            message = ValidationMessagesConstants.Validation.APELLIDOS_LONGITUD)
    @Pattern(regexp = BusinessConstants.Patterns.NOMBRES_APELLIDOS,
            message = ValidationMessagesConstants.Validation.APELLIDOS_FORMATO)
    private String apellidos;

    @NotBlank(message = ValidationMessagesConstants.Validation.EMAIL_REQUERIDO)
    @Email(message = ValidationMessagesConstants.Validation.EMAIL_FORMATO)
    @Size(max = BusinessConstants.Texto.EMAIL_MAX_LENGTH,
            message = ValidationMessagesConstants.Validation.EMAIL_LONGITUD)
    private String email;

    @NotBlank(message = ValidationMessagesConstants.Validation.TELEFONO_REQUERIDO)
    @Pattern(regexp = BusinessConstants.Patterns.TELEFONO,
            message = ValidationMessagesConstants.Validation.TELEFONO_FORMATO)
    private String telefono;

    @NotNull(message = ValidationMessagesConstants.Validation.MONTO_REQUERIDO)
    @DecimalMin(value = "100000", message = ValidationMessagesConstants.Validation.MONTO_MINIMO)
    @DecimalMax(value = "50000000", message = ValidationMessagesConstants.Validation.MONTO_MAXIMO)
    @Digits(integer = 15, fraction = 2, message = ValidationMessagesConstants.Validation.MONTO_FORMATO)
    private BigDecimal montoSolicitado;

    @NotNull(message = ValidationMessagesConstants.Validation.PLAZO_REQUERIDO)
    @Min(value = BusinessConstants.Financiero.PLAZO_MINIMO_MESES,
            message = ValidationMessagesConstants.Validation.PLAZO_MINIMO)
    @Max(value = BusinessConstants.Financiero.PLAZO_MAXIMO_MESES,
            message = ValidationMessagesConstants.Validation.PLAZO_MAXIMO)
    private Integer plazoMeses;

    @NotBlank(message = ValidationMessagesConstants.Validation.TIPO_CREDITO_REQUERIDO)
    @Pattern(regexp = BusinessConstants.TipoCredito.TIPOS_REGEX,
            message = ValidationMessagesConstants.Validation.TIPO_CREDITO_INVALIDO)
    private String tipoCredito;

    // Campos opcionales para evaluación de capacidad de pago
    @DecimalMin(value = "0", message = ValidationMessagesConstants.Validation.INGRESOS_NEGATIVOS)
    @Digits(integer = 15, fraction = 2, message = ValidationMessagesConstants.Validation.INGRESOS_FORMATO)
    private BigDecimal ingresosMensuales;

    @DecimalMin(value = "0", message = ValidationMessagesConstants.Validation.GASTOS_NEGATIVOS)
    @Digits(integer = 15, fraction = 2, message = ValidationMessagesConstants.Validation.GASTOS_FORMATO)
    private BigDecimal gastosMensuales;
}