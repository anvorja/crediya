// infrastructure/entry-points/reactive-web/src/main/java/com/crediya/solicitudes/api/RouterRest.java
package com.crediya.solicitudes.api;

import com.crediya.solicitudes.api.constants.ApiRoutesConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Configuración de rutas WebFlux usando Router Functions
 * Implementa los endpoints de la HU2 de forma reactiva
 */
@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                // ========================================
                // HU2: REGISTRAR SOLICITUD DE PRÉSTAMO
                // ========================================
                .POST(ApiRoutesConstants.Solicitudes.BASE,
                        accept(MediaType.APPLICATION_JSON).and(contentType(MediaType.APPLICATION_JSON)),
                        handler::crearSolicitud)

                // ========================================
                // ENDPOINTS DE CONSULTA (FUTURAS HUs)
                // ========================================
                .GET(ApiRoutesConstants.Solicitudes.BY_ID,
                        accept(MediaType.APPLICATION_JSON),
                        handler::consultarSolicitud)

                .GET(ApiRoutesConstants.Solicitudes.BY_DOCUMENTO,
                        accept(MediaType.APPLICATION_JSON),
                        handler::consultarPorDocumento)

                // ========================================
                // HEALTH CHECK MEJORADO
                // ========================================
                .GET(ApiRoutesConstants.Health.HEALTH,
                        request -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new HealthResponse(
                                        "UP",
                                        "microservicio-solicitudes",
                                        "HU2 - Registrar solicitud de préstamo",
                                        java.time.LocalDateTime.now()
                                )))

                .build();
    }

    /**
     * DTO para respuesta de health check
     */
    public record HealthResponse(
            String status,
            String service,
            String description,
            java.time.LocalDateTime timestamp
    ) {}
}