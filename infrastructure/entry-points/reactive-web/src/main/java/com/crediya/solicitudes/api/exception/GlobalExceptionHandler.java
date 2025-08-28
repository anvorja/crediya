package com.crediya.solicitudes.api.exception;

import com.crediya.solicitudes.api.dto.response.ErrorResponse;
import com.crediya.solicitudes.model.solicitud.exception.DatosSolicitudInvalidosException;
import com.crediya.solicitudes.model.solicitud.exception.SolicitudDuplicadaException;
import com.crediya.solicitudes.model.solicitud.exception.SolicitudNoEncontradaException;
import com.crediya.solicitudes.model.solicitud.exception.TransicionEstadoInvalidaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({WebExchangeBindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        Map<String, String> errores = new HashMap<>();

        if (ex instanceof WebExchangeBindException webEx) {
            webEx.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errores.put(fieldName, errorMessage);
            });
        } else if (ex instanceof MethodArgumentNotValidException methodEx) {
            methodEx.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errores.put(fieldName, errorMessage);
            });
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Datos de entrada inválidos")
                .message("Los datos proporcionados no cumplen con las validaciones requeridas")
                .details(errores)
                .build();

        log.warn("Errores de validación: {}", errores);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DatosSolicitudInvalidosException.class)
    public ResponseEntity<ErrorResponse> handleDatosSolicitudInvalidos(DatosSolicitudInvalidosException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Datos de solicitud inválidos")
                .message(ex.getMessage())
                .build();

        log.warn("Datos de solicitud inválidos: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(SolicitudDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudDuplicada(SolicitudDuplicadaException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Solicitud duplicada")
                .message(ex.getMessage())
                .build();

        log.warn("Solicitud duplicada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(SolicitudNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudNoEncontrada(SolicitudNoEncontradaException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Solicitud no encontrada")
                .message(ex.getMessage())
                .build();

        log.warn("Solicitud no encontrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicionEstadoInvalida(TransicionEstadoInvalidaException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Transición de estado inválida")
                .message(ex.getMessage())
                .build();

        log.warn("Transición de estado inválida: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Error no manejado - Tipo: {}, Mensaje: {}",
                ex.getClass().getSimpleName(), ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error interno del servidor")
                .message("Ha ocurrido un error inesperado. Por favor contacte al administrador.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}