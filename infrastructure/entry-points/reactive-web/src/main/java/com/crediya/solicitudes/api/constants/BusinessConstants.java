package com.crediya.solicitudes.api.constants;

import java.math.BigDecimal;

public class BusinessConstants {

    private BusinessConstants() {
        // Prevenir instanciación
    }

    // Límites de documentos
    public static final class Documento {
        private Documento() {}

        public static final int LONGITUD_MINIMA = 6;
        public static final int LONGITUD_MAXIMA = 15;
    }

    // Límites de texto
    public static final class Texto {
        private Texto() {}

        public static final int NOMBRES_MAX_LENGTH = 100;
        public static final int APELLIDOS_MAX_LENGTH = 100;
        public static final int EMAIL_MAX_LENGTH = 150;
        public static final int TELEFONO_MIN_LENGTH = 10;
        public static final int TELEFONO_MAX_LENGTH = 15;
        public static final int OBSERVACIONES_MAX_LENGTH = 500;
    }

    // Límites financieros
    public static final class Financiero {
        private Financiero() {}

        public static final BigDecimal MONTO_MINIMO = new BigDecimal("100000");
        public static final BigDecimal MONTO_MAXIMO = new BigDecimal("50000000");
        public static final int PLAZO_MINIMO_MESES = 6;
        public static final int PLAZO_MAXIMO_MESES = 72;
        public static final BigDecimal PORCENTAJE_GASTOS_MAXIMO = new BigDecimal("70");
        public static final BigDecimal MULTIPLICADOR_CAPACIDAD_MAXIMA = new BigDecimal("5");
        public static final BigDecimal PORCENTAJE_CAPACIDAD_ENDEUDAMIENTO = new BigDecimal("0.30");
        public static final int PUNTAJE_CREDITICIO_MINIMO = 500;
    }

    // Tipos de crédito permitidos
    public static final class TipoCredito {
        private TipoCredito() {}

        public static final String PERSONAL = "PERSONAL";
        public static final String VEHICULO = "VEHICULO";
        public static final String VIVIENDA = "VIVIENDA";
        public static final String EDUCATIVO = "EDUCATIVO";

        // Regex para validación
        public static final String TIPOS_REGEX = "^(" + PERSONAL + "|" + VEHICULO + "|" + VIVIENDA + "|" + EDUCATIVO + ")$";
    }

    // Estados de solicitud
    public static final class Estados {
        private Estados() {}

        public static final String PENDIENTE_REVISION = "PENDIENTE_REVISION";
        public static final String PRE_APROBADA = "PRE_APROBADA";
        public static final String APROBADA = "APROBADA";
        public static final String RECHAZADA = "RECHAZADA";
    }

    // Regex patterns
    public static final class Patterns {
        private Patterns() {}

        public static final String SOLO_NUMEROS = "^[0-9]+$";
        public static final String NOMBRES_APELLIDOS = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$";
        public static final String TELEFONO = "^\\+?[0-9]{10,15}$";
    }
}
