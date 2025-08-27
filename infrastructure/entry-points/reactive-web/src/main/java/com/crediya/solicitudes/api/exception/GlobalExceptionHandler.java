package com.crediya.solicitudes.api.exception;

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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST
 * Convierte excepciones del dominio en respuestas HTTP apropiadas
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Manejo de errores de validación de datos de entrada (DTOs)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Datos de entrada inválidos")
                .message("Los datos proporcionados no cumplen con las validaciones requeridas")
                .details(errores)
                .build();

        log.warn("🚨 Errores de validación: {}", errores);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Manejo de datos de solicitud inválidos (dominio)
     */
    @ExceptionHandler(DatosSolicitudInvalidosException.class)
    public ResponseEntity<ErrorResponse> handleDatosSolicitudInvalidos(DatosSolicitudInvalidosException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Datos de solicitud inválidos")
                .message(ex.getMessage())
                .build();

        log.warn("🚨 Datos de solicitud inválidos: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Manejo de solicitud duplicada
     */
    @ExceptionHandler(SolicitudDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudDuplicada(SolicitudDuplicadaException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Solicitud duplicada")
                .message(ex.getMessage())
                .build();

        log.warn("🚨 Solicitud duplicada: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Manejo de solicitud no encontrada
     */
    @ExceptionHandler(SolicitudNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudNoEncontrada(SolicitudNoEncontradaException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Solicitud no encontrada")
                .message(ex.getMessage())
                .build();

        log.warn("🚨 Solicitud no encontrada: {}", ex.getMessage());

        return ResponseEntity.notFound().build();
    }

    /**
     * Manejo de transición de estado inválida
     */
    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicionEstadoInvalida(TransicionEstadoInvalidaException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Transición de estado inválida")
                .message(ex.getMessage())
                .build();

        log.warn("🚨 Transición de estado inválida: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Manejo de errores internos no controlados
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error interno del servidor")
                .message("Ha ocurrido un error inesperado. Por favor contacte al administrador.")
                .build();

        log.error("🚨 Error interno no controlado: {}", ex.getMessage(), ex);

        return ResponseEntity.internalServerError().body(errorResponse);
    }

    /**
     * DTO para respuestas de error estructuradas
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> details;

        // Builder pattern para construcción fluida
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }

        public static class ErrorResponseBuilder {
            private LocalDateTime timestamp;
            private int status;
            private String error;
            private String message;
            private Map<String, String> details;

            public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public ErrorResponseBuilder status(int status) {
                this.status = status;
                return this;
            }

            public ErrorResponseBuilder error(String error) {
                this.error = error;
                return this;
            }

            public ErrorResponseBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ErrorResponseBuilder details(Map<String, String> details) {
                this.details = details;
                return this;
            }

            public ErrorResponse build() {
                ErrorResponse response = new ErrorResponse();
                response.timestamp = this.timestamp;
                response.status = this.status;
                response.error = this.error;
                response.message = this.message;
                response.details = this.details;
                return response;
            }
        }

        // Getters y setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Map<String, String> getDetails() { return details; }
        public void setDetails(Map<String, String> details) { this.details = details; }
    }
}