package com.crediya.solicitudes.model.solicitud.exception;

public class SolicitudNoEncontradaException extends RuntimeException {

    public SolicitudNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public SolicitudNoEncontradaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}