package com.crediya.solicitudes.model.solicitud.exception;

public class SolicitudDuplicadaException extends RuntimeException {

    public SolicitudDuplicadaException(String mensaje) {
        super(mensaje);
    }

    public SolicitudDuplicadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}