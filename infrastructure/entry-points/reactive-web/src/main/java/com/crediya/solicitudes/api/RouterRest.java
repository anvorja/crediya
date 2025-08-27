// infrastructure/entry-points/reactive-web/src/main/java/com/crediya/solicitudes/api/RouterRest.java
package com.crediya.solicitudes.api;

import com.crediya.solicitudes.api.constants.ApiRoutesConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Configuración de rutas usando Router Functions (estilo funcional de WebFlux)
 * Utiliza constantes centralizadas para facilitar mantenimiento
 */
@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return
                // POST /api/v1/solicitud - Crear nueva solicitud
                route(POST(ApiRoutesConstants.Solicitudes.BASE)
                                .and(contentType(org.springframework.http.MediaType.APPLICATION_JSON)),
                        handler::crearSolicitud)

                        // GET /api/v1/solicitud/{id} - Consultar solicitud por ID
                        .andRoute(GET(ApiRoutesConstants.Solicitudes.BY_ID),
                                handler::consultarSolicitud)

                        // GET /api/v1/solicitud/documento/{numeroDocumento} - Consultar por documento
                        .andRoute(GET(ApiRoutesConstants.Solicitudes.BY_DOCUMENTO),
                                handler::consultarPorDocumento)

                        // Health check endpoint
                        .andRoute(GET(ApiRoutesConstants.Health.HEALTH),
                                request -> ServerResponse.ok()
                                        .bodyValue("{ \"status\": \"UP\", \"service\": \"microservicio-solicitudes\" }"));
    }
}
