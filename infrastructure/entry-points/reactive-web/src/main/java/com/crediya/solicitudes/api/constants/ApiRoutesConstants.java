package com.crediya.solicitudes.api.constants;

/**
 * Constantes para rutas de API
 * Centralización de URLs para facilitar mantenimiento
 */
public class ApiRoutesConstants {

    private ApiRoutesConstants() {
        // Prevenir instanciación
    }

    // Rutas base
    public static final String API_V1 = "/api/v1";

    // Módulo de solicitudes
    public static final class Solicitudes {
        private Solicitudes() {}

        public static final String BASE = API_V1 + "/solicitud";
        public static final String BY_ID = BASE + "/{id}";
        public static final String BY_DOCUMENTO = BASE + "/documento/{numeroDocumento}";
    }

    // Rutas de salud y monitoreo
    public static final class Health {
        private Health() {}

        public static final String HEALTH = "/api/health";
        public static final String ACTUATOR_HEALTH = "/actuator/health";
    }
}
