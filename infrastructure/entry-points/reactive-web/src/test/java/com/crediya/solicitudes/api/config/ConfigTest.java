// infrastructure/entry-points/reactive-web/src/test/java/com/crediya/solicitudes/api/config/ConfigTest.java
package com.crediya.solicitudes.api.config;

import com.crediya.solicitudes.api.Handler;
import com.crediya.solicitudes.api.RouterRest;
import com.crediya.solicitudes.api.mapper.SolicitudRestMapper;
import com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    private WebTestClient webTestClient;

    @Mock
    private CrearSolicitudUseCase crearSolicitudUseCase;

    @Mock
    private SolicitudRestMapper solicitudRestMapper;

    @BeforeEach
    void setUp() {
        // Creamos el Handler con mocks
        Handler handler = new Handler(crearSolicitudUseCase, solicitudRestMapper);

        // Creamos el router
        RouterRest routerRest = new RouterRest();

        // WebTestClient con filters de seguridad simulados
        webTestClient = WebTestClient
                .bindToRouterFunction(routerRest.routerFunction(handler))
                .webFilter(new CorsConfig().corsWebFilter("http://localhost:4200,http://localhost:8080"))
                .webFilter(new SecurityHeadersConfig())
                .build();
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.get()
                .uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}