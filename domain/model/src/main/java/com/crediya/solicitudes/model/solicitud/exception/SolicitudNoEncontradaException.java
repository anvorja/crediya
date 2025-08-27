package com.crediya.solicitudes.model.solicitud.exception;

public class SolicitudNoEncontradaException extends RuntimeException {
    public SolicitudNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}