// com/crediya/solicitudes/model/solicitud/exception/DatosSolicitudInvalidosException.java
package com.crediya.solicitudes.model.solicitud.exception;

public class DatosSolicitudInvalidosException extends RuntimeException {
    public DatosSolicitudInvalidosException(String mensaje) {
        super(mensaje);
    }
}