// infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/com/crediya/solicitudes/r2dbc/config/PostgresqlConnectionProperties.java
package com.crediya.solicitudes.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración para la conexión R2DBC a PostgreSQL
 * Carga las propiedades desde application.yaml
 */
@ConfigurationProperties(prefix = "adapters.r2dbc.postgresql")
public record PostgresqlConnectionProperties(
        String host,
        Integer port,
        String database,
        String schema,
        String username,
        String password
) {

    /**
     * Constructor con valores por defecto para desarrollo local
     */
    public PostgresqlConnectionProperties() {
        this("localhost", 5432, "crediya_solicitudes", "public", "postgres", "superapostgres");
    }

    /**
     * Validación de propiedades
     */
    public void validate() {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Database host is required");
        }
        if (port == null || port < 1 || port > 65535) {
            throw new IllegalArgumentException("Database port must be between 1 and 65535");
        }
        if (database == null || database.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name is required");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Database username is required");
        }
        if (password == null) {
            throw new IllegalArgumentException("Database password is required");
        }
    }
}