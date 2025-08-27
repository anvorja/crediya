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

                route(POST(ApiRoutesConstants.Solicitudes.BASE)
                                .and(contentType(org.springframework.http.MediaType.APPLICATION_JSON)),
                        handler::crearSolicitud)

                        .andRoute(GET(ApiRoutesConstants.Solicitudes.BY_ID),
                                handler::consultarSolicitud)

                        .andRoute(GET(ApiRoutesConstants.Solicitudes.BY_DOCUMENTO),
                                handler::consultarPorDocumento)


                        .andRoute(GET(ApiRoutesConstants.Health.HEALTH),
                                request -> ServerResponse.ok()
                                        .bodyValue("{ \"status\": \"UP\", \"service\": \"microservicio-solicitudes\" }"));

    }
}