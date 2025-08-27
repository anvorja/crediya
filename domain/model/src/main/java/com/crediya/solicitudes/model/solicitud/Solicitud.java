
// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/Solicitud.java
package com.crediya.solicitudes.model.solicitud;

import com.crediya.solicitudes.model.solicitud.exception.DatosSolicitudInvalidosException;
import com.crediya.solicitudes.model.solicitud.exception.TransicionEstadoInvalidaException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class Solicitud {

    private String id;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private BigDecimal montoSolicitado;
    private String tipoCredito;
    private EstadoSolicitud estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String observaciones;
    private BigDecimal ingresosMensuales;
    private BigDecimal gastosMensuales;

    // Constructor por defecto
    public Solicitud() {
        this.id = UUID.randomUUID().toString();
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoSolicitud.PENDIENTE_REVISION;
    }

    // Constructor completo
    public Solicitud(String id, String numeroDocumento, String nombres, String apellidos,
                     String email, String telefono, BigDecimal montoSolicitado, String tipoCredito,
                     EstadoSolicitud estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                     String observaciones, BigDecimal ingresosMensuales, BigDecimal gastosMensuales) {
        this.id = id;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.montoSolicitado = montoSolicitado;
        this.tipoCredito = tipoCredito;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.observaciones = observaciones;
        this.ingresosMensuales = ingresosMensuales;
        this.gastosMensuales = gastosMensuales;
    }

    // ============================================================
    // GETTERS Y SETTERS (sin Lombok)
    // ============================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public void setMontoSolicitado(BigDecimal montoSolicitado) { this.montoSolicitado = montoSolicitado; }

    public String getTipoCredito() { return tipoCredito; }
    public void setTipoCredito(String tipoCredito) { this.tipoCredito = tipoCredito; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public BigDecimal getIngresosMensuales() { return ingresosMensuales; }
    public void setIngresosMensuales(BigDecimal ingresosMensuales) { this.ingresosMensuales = ingresosMensuales; }

    public BigDecimal getGastosMensuales() { return gastosMensuales; }
    public void setGastosMensuales(BigDecimal gastosMensuales) { this.gastosMensuales = gastosMensuales; }

    // ============================================================
    // MÉTODOS DE NEGOCIO (Domain Logic) - SIN DEPENDENCIAS EXTERNAS
    // ============================================================

    public void validarDatos() {
        if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("El número de documento es obligatorio");
        }

        if (numeroDocumento.length() < 6 || numeroDocumento.length() > 15) {
            throw new DatosSolicitudInvalidosException("El número de documento debe tener entre 6 y 15 caracteres");
        }

        if (nombres == null || nombres.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("Los nombres son obligatorios");
        }

        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("Los apellidos son obligatorios");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new DatosSolicitudInvalidosException("El email no tiene un formato válido");
        }

        if (telefono == null || !telefono.matches("\\+?[0-9]{10,15}")) {
            throw new DatosSolicitudInvalidosException("El teléfono debe tener entre 10 y 15 dígitos");
        }

        validarMonto();
        validarTipoCredito();
    }

    private void validarMonto() {
        if (montoSolicitado == null) {
            throw new DatosSolicitudInvalidosException("El monto solicitado es obligatorio");
        }

        BigDecimal montoMinimo = new BigDecimal("100000");
        BigDecimal montoMaximo = new BigDecimal("50000000");

        if (montoSolicitado.compareTo(montoMinimo) < 0) {
            throw new DatosSolicitudInvalidosException("El monto mínimo a solicitar es $100,000");
        }

        if (montoSolicitado.compareTo(montoMaximo) > 0) {
            throw new DatosSolicitudInvalidosException("El monto máximo a solicitar es $50,000,000");
        }
    }

    private void validarTipoCredito() {
        if (tipoCredito == null || tipoCredito.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("El tipo de crédito es obligatorio");
        }

        Set<String> tiposPermitidos = Set.of("PERSONAL", "VEHICULO", "VIVIENDA", "EDUCATIVO");
        if (!tiposPermitidos.contains(tipoCredito.toUpperCase())) {
            throw new DatosSolicitudInvalidosException(
                    "Tipo de crédito no válido. Tipos permitidos: " + String.join(", ", tiposPermitidos)
            );
        }
    }

    public boolean evaluarCapacidadPago() {
        if (ingresosMensuales == null || gastosMensuales == null) {
            return false;
        }

        // Regla 1: Gastos no pueden superar 70% de ingresos
        BigDecimal porcentajeGastos = gastosMensuales
                .divide(ingresosMensuales, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));

        if (porcentajeGastos.compareTo(new BigDecimal("70")) > 0) {
            this.observaciones = "Gastos superan el 70% de los ingresos";
            return false;
        }

        // Regla 2: Monto solicitado no puede superar 5 veces los ingresos mensuales
        BigDecimal capacidadMaxima = ingresosMensuales.multiply(new BigDecimal("5"));
        if (montoSolicitado.compareTo(capacidadMaxima) > 0) {
            this.observaciones = "Monto solicitado supera 5 veces los ingresos mensuales";
            return false;
        }

        return true;
    }

    public void cambiarEstado(EstadoSolicitud nuevoEstado, String observaciones) {
        if (!this.estado.puedeTransicionarA(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(
                    String.format("No se puede cambiar de %s a %s", this.estado, nuevoEstado)
            );
        }

        this.estado = nuevoEstado;
        this.observaciones = observaciones;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void preAprobarAutomaticamente() {
        if (evaluarCapacidadPago()) {
            cambiarEstado(EstadoSolicitud.PRE_APROBADA, "Pre-aprobada automáticamente por el sistema");
        } else {
            this.observaciones = observaciones != null ? observaciones : "Requiere evaluación manual";
            this.fechaActualizacion = LocalDateTime.now();
        }
    }

    public boolean puedeSerEditada() {
        return estado == EstadoSolicitud.PENDIENTE_REVISION;
    }

    public boolean estaEnEstadoFinal() {
        return estado == EstadoSolicitud.APROBADA || estado == EstadoSolicitud.RECHAZADA;
    }

    public BigDecimal calcularCapacidadEndeudamiento() {
        if (ingresosMensuales == null || gastosMensuales == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal ingresoDisponible = ingresosMensuales.subtract(gastosMensuales);
        return ingresoDisponible.multiply(new BigDecimal("0.30"));
    }

    public String getNombreCompleto() {
        return String.format("%s %s", nombres, apellidos);
    }

    public boolean esNueva() {
        return id == null || estado == EstadoSolicitud.PENDIENTE_REVISION;
    }

    @Override
    public String toString() {
        return String.format("Solicitud{id='%s', documento='%s', estado=%s, monto=%s}",
                id, numeroDocumento, estado, montoSolicitado);
    }
}