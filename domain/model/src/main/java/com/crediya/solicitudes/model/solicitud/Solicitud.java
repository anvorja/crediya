// domain/model/src/main/java/com/crediya/solicitudes/model/solicitud/Solicitud.java
package com.crediya.solicitudes.model.solicitud;

import com.crediya.solicitudes.model.constants.BusinessConstants;
import com.crediya.solicitudes.model.solicitud.exception.DatosSolicitudInvalidosException;
import com.crediya.solicitudes.model.solicitud.exception.TransicionEstadoInvalidaException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Entidad de dominio Solicitud
 * Representa una solicitud de préstamo con todas sus validaciones de negocio
 * Criterios de Aceptación HU2:
 * - CA-1: Validar datos completos del cliente y préstamo
 * - CA-2: Asignar estado inicial PENDIENTE_REVISION
 * - CA-3: Validar tipos de préstamo permitidos
 */
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
    private Integer plazoMeses; // AÑADIDO

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
                     String observaciones, BigDecimal ingresosMensuales, BigDecimal gastosMensuales,
                     Integer plazoMeses) {
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
        this.plazoMeses = plazoMeses;
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

    public Integer getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(Integer plazoMeses) { this.plazoMeses = plazoMeses; }

    public void validarDatos() {
        validarCamposObligatorios();
        validarFormatoDocumento();
        validarFormatoNombres();
        validarFormatoApellidos();
        validarFormatoEmail();
        validarFormatoTelefono();
        validarMonto();
        validarTipoCredito();
        validarPlazo();
        validarDatosFinancieros();
    }

    private void validarCamposObligatorios() {
        if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("El número de documento es obligatorio");
        }
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("Los nombres son obligatorios");
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("Los apellidos son obligatorios");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("El email es obligatorio");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("El teléfono es obligatorio");
        }
        if (montoSolicitado == null) {
            throw new DatosSolicitudInvalidosException("El monto solicitado es obligatorio");
        }
        if (tipoCredito == null || tipoCredito.trim().isEmpty()) {
            throw new DatosSolicitudInvalidosException("El tipo de crédito es obligatorio");
        }
        if (plazoMeses == null) {
            throw new DatosSolicitudInvalidosException("El plazo en meses es obligatorio");
        }
    }

    private void validarFormatoDocumento() {
        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.DOCUMENTO);
        if (!pattern.matcher(numeroDocumento.trim()).matches()) {
            throw new DatosSolicitudInvalidosException("El formato del documento no es válido. Debe contener entre " +
                    BusinessConstants.Documento.LONGITUD_MINIMA + " y " +
                    BusinessConstants.Documento.LONGITUD_MAXIMA + " dígitos");
        }
    }

    private void validarFormatoNombres() {
        if (nombres.trim().length() > BusinessConstants.Texto.NOMBRES_MAX_LENGTH) {
            throw new DatosSolicitudInvalidosException("Los nombres no pueden exceder " +
                    BusinessConstants.Texto.NOMBRES_MAX_LENGTH + " caracteres");
        }

        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.NOMBRES_APELLIDOS);
        if (!pattern.matcher(nombres.trim()).matches()) {
            throw new DatosSolicitudInvalidosException("Los nombres solo pueden contener letras y espacios");
        }
    }

    private void validarFormatoApellidos() {
        if (apellidos.trim().length() > BusinessConstants.Texto.APELLIDOS_MAX_LENGTH) {
            throw new DatosSolicitudInvalidosException("Los apellidos no pueden exceder " +
                    BusinessConstants.Texto.APELLIDOS_MAX_LENGTH + " caracteres");
        }

        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.NOMBRES_APELLIDOS);
        if (!pattern.matcher(apellidos.trim()).matches()) {
            throw new DatosSolicitudInvalidosException("Los apellidos solo pueden contener letras y espacios");
        }
    }

    private void validarFormatoEmail() {
        if (email.trim().length() > BusinessConstants.Texto.EMAIL_MAX_LENGTH) {
            throw new DatosSolicitudInvalidosException("El email no puede exceder " +
                    BusinessConstants.Texto.EMAIL_MAX_LENGTH + " caracteres");
        }

        // Validación de formato de email más robusta
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email.trim()).matches()) {
            throw new DatosSolicitudInvalidosException("El formato del email no es válido");
        }
    }

    private void validarFormatoTelefono() {
        Pattern pattern = Pattern.compile(BusinessConstants.Patterns.TELEFONO);
        if (!pattern.matcher(telefono.trim()).matches()) {
            throw new DatosSolicitudInvalidosException("El formato del teléfono no es válido. " +
                    "Debe tener entre " + BusinessConstants.Texto.TELEFONO_MIN_LENGTH +
                    " y " + BusinessConstants.Texto.TELEFONO_MAX_LENGTH + " dígitos");
        }
    }

    private void validarMonto() {
        if (montoSolicitado.compareTo(BusinessConstants.Financiero.MONTO_MINIMO) < 0) {
            throw new DatosSolicitudInvalidosException("El monto mínimo permitido es $" +
                    BusinessConstants.Financiero.MONTO_MINIMO.toPlainString());
        }

        if (montoSolicitado.compareTo(BusinessConstants.Financiero.MONTO_MAXIMO) > 0) {
            throw new DatosSolicitudInvalidosException("El monto máximo permitido es $" +
                    BusinessConstants.Financiero.MONTO_MAXIMO.toPlainString());
        }
    }

    /**
     * CA-3: Validar tipos de préstamo permitidos
     * Este método implementa el criterio de aceptación específico sobre tipos válidos
     * DIFERENCIA entre tipo vacío (obligatorio) vs tipo inválido
     */
//    private void validarTipoCredito() {
//        // Primero verificar si está vacío (diferente semántica)
//        if (tipoCredito == null || tipoCredito.trim().isEmpty()) {
//            throw new DatosSolicitudInvalidosException("El tipo de crédito es obligatorio");
//        }
//
//        // Luego validar si el tipo existe en los permitidos
//        if (!BusinessConstants.TipoCredito.TIPOS_VALIDOS.contains(tipoCredito)) {
//            throw new DatosSolicitudInvalidosException("Tipo de crédito no válido: " + tipoCredito +
//                    ". Tipos permitidos: " + String.join(", ", BusinessConstants.TipoCredito.TIPOS_VALIDOS));
//        }
//    }
    private void validarTipoCredito() {
        if (tipoCredito == null) {
            throw new DatosSolicitudInvalidosException("El tipo de crédito es obligatorio");
        }

        // String vacío O tipo no existe = "no válido" (no "obligatorio")
        if (tipoCredito.trim().isEmpty() || !BusinessConstants.TipoCredito.TIPOS_VALIDOS.contains(tipoCredito)) {
            throw new DatosSolicitudInvalidosException("Tipo de crédito no válido: " + tipoCredito +
                    ". Tipos permitidos: " + String.join(", ", BusinessConstants.TipoCredito.TIPOS_VALIDOS));
        }
    }

    private void validarPlazo() {
        if (plazoMeses < BusinessConstants.Plazo.MINIMO_MESES) {
            throw new DatosSolicitudInvalidosException("El plazo mínimo es de " +
                    BusinessConstants.Plazo.MINIMO_MESES + " meses");
        }

        // Validación específica según tipo de crédito
        int plazoMaximo = obtenerPlazoMaximoSegunTipo(tipoCredito);
        if (plazoMeses > plazoMaximo) {
            throw new DatosSolicitudInvalidosException("El plazo máximo para " + tipoCredito +
                    " es de " + plazoMaximo + " meses");
        }
    }

    /**
     * Obtiene el plazo máximo según el tipo de crédito
     * Reglas de negocio específicas por tipo
     */
    private int obtenerPlazoMaximoSegunTipo(String tipoCredito) {
        return switch (tipoCredito) {
            case "PERSONAL" -> 60;      // 5 años
            case "VEHICULO" -> 96;      // 8 años
            case "VIVIENDA" -> 360;     // 30 años
            case "EDUCATIVO" -> 120;    // 10 años
            default -> BusinessConstants.Plazo.MAXIMO_MESES; // 72 meses por defecto
        };
    }

    private void validarDatosFinancieros() {
        if (ingresosMensuales != null && ingresosMensuales.compareTo(BigDecimal.ZERO) < 0) {
            throw new DatosSolicitudInvalidosException("Los ingresos mensuales no pueden ser negativos");
        }

        if (gastosMensuales != null && gastosMensuales.compareTo(BigDecimal.ZERO) < 0) {
            throw new DatosSolicitudInvalidosException("Los gastos mensuales no pueden ser negativos");
        }
    }

    /**
     * Lógica de transiciones de estado permitidas
     */
    private boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
        return switch (this.estado) {
            case PENDIENTE_REVISION -> nuevoEstado == EstadoSolicitud.EN_REVISION ||
                    nuevoEstado == EstadoSolicitud.RECHAZADA;
            case EN_REVISION -> nuevoEstado == EstadoSolicitud.APROBADA ||
                    nuevoEstado == EstadoSolicitud.RECHAZADA;
            case APROBADA, RECHAZADA -> false; // Estados finales
        };
    }

    /**
     * Cambio de estado con validaciones
     */
    public void cambiarEstado(EstadoSolicitud nuevoEstado, String observaciones) {
        if (!puedeTransicionarA(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(
                    String.format("No se puede cambiar de %s a %s", this.estado, nuevoEstado)
            );
        }
        this.estado = nuevoEstado;
        this.observaciones = observaciones; // Agregar esta línea
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void asignarEstadoInicial() {
        this.estado = EstadoSolicitud.PENDIENTE_REVISION;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean puedeSerEditada() {
        return estado == EstadoSolicitud.PENDIENTE_REVISION || estado == EstadoSolicitud.EN_REVISION;
    }


    public boolean estaEnEstadoFinal() {
        return estado == EstadoSolicitud.APROBADA || estado == EstadoSolicitud.RECHAZADA;
    }

    public BigDecimal calcularCapacidadEndeudamiento() {
        if (ingresosMensuales == null || gastosMensuales == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal ingresoDisponible = ingresosMensuales.subtract(gastosMensuales);
        return ingresoDisponible.multiply(BusinessConstants.Financiero.PORCENTAJE_CAPACIDAD_ENDEUDAMIENTO);
    }

    public boolean evaluarCapacidadPago() {
        if (ingresosMensuales == null || gastosMensuales == null) {
            return false;
        }

        // Regla 1: Gastos no pueden superar 70% de ingresos - CORREGIDO RoundingMode
        BigDecimal porcentajeGastos = gastosMensuales
                .divide(ingresosMensuales, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        if (porcentajeGastos.compareTo(BusinessConstants.Financiero.PORCENTAJE_GASTOS_MAXIMO) > 0) {
            this.observaciones = "Gastos superan el 70% de los ingresos";
            return false;
        }

        // Regla 2: Monto solicitado no puede superar 5 veces los ingresos mensuales
        BigDecimal capacidadMaxima = ingresosMensuales.multiply(BusinessConstants.Financiero.MULTIPLICADOR_CAPACIDAD_MAXIMA);
        if (montoSolicitado.compareTo(capacidadMaxima) > 0) {
            this.observaciones = "Monto solicitado supera 5 veces los ingresos mensuales";
            return false;
        }

        return true;
    }

    /**
     * Método para pruebas - evalúa si tiene capacidad de pago suficiente
     */
    public boolean tieneCapacidadPago() {
        if (ingresosMensuales == null || gastosMensuales == null || montoSolicitado == null) {
            return false;
        }

        // REGLA 1: Los gastos no pueden superar el 70% de los ingresos
        BigDecimal porcentajeGastos = gastosMensuales
                .divide(ingresosMensuales, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        if (porcentajeGastos.compareTo(BusinessConstants.Financiero.PORCENTAJE_GASTOS_MAXIMO) > 0) {
            return false; // Automáticamente no tiene capacidad si gastos > 70%
        }

        // REGLA 2: Calcular capacidad de endeudamiento del ingreso disponible
        BigDecimal ingresoDisponible = ingresosMensuales.subtract(gastosMensuales);

        // Si los gastos son mayores o iguales a los ingresos, no tiene capacidad
        if (ingresoDisponible.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // La capacidad de endeudamiento es el 30% del ingreso disponible
        BigDecimal capacidadEndeudamiento = ingresoDisponible.multiply(BusinessConstants.Financiero.FACTOR_ENDEUDAMIENTO);

        // Calcular cuota mínima mensual estimada (5% del monto)
        BigDecimal cuotaMinima = montoSolicitado.multiply(new BigDecimal("0.05"));

        // Tiene capacidad si puede pagar al menos la cuota mínima
        return capacidadEndeudamiento.compareTo(cuotaMinima) >= 0;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String generarResumen() {
        // Usar Locale.US para forzar comas como separadores de miles
        DecimalFormat formatter = new DecimalFormat("#,###",
                DecimalFormatSymbols.getInstance(Locale.US));

        return String.format("Solicitud %s - %s - %s - $%s - %s",
                id, getNombreCompleto(), tipoCredito,
                formatter.format(montoSolicitado), estado.name());
    }

    @Override
    public String toString() {
        return String.format("Solicitud{id='%s', documento='%s', estado=%s, monto=%s}",
                id, numeroDocumento, estado, montoSolicitado);
    }

    // ========================================
    // MÉTODOS UTILITARIOS
    // ========================================

    public boolean esSolicitudActiva() {
        return estado != EstadoSolicitud.RECHAZADA;
    }

    public boolean esSolicitudPendiente() {
        return estado == EstadoSolicitud.PENDIENTE_REVISION;
    }
}