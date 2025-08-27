// com/crediya/solicitudes/model/solicitud/exception/TransicionEstadoInvalidaException.java
package com.crediya.solicitudes.model.solicitud.exception;

public class TransicionEstadoInvalidaException extends RuntimeException {
    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}