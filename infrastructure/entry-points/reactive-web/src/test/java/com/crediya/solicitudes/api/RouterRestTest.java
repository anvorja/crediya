// infrastructure/entry-points/reactive-web/src/test/java/com/crediya/solicitudes/api/RouterRestTest.java
package com.crediya.solicitudes.api;

import com.crediya.solicitudes.api.mapper.SolicitudRestMapper;
import com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    private WebTestClient webTestClient;

    @Mock
    private CrearSolicitudUseCase crearSolicitudUseCase;

    @Mock
    private SolicitudRestMapper solicitudRestMapper;

    @BeforeEach
    void setUp() {
        // Creamos el Handler con mocks
        Handler handler = new Handler(crearSolicitudUseCase, solicitudRestMapper);

        // Creamos el router con el handler mockeado
        RouterRest routerRest = new RouterRest();

        // Configuramos WebTestClient
        webTestClient = WebTestClient
                .bindToRouterFunction(routerRest.routerFunction(handler))
                .build();
    }

    @Test
    void testHealthEndpoint() {
        webTestClient.get()
                .uri("/api/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> Assertions.assertThat(response)
                        .contains("\"status\": \"UP\"")
                        .contains("\"service\": \"microservicio-solicitudes\""));
    }

    @Test
    void testConsultarSolicitudEndpoint() {
        webTestClient.get()
                .uri("/api/v1/solicitud/123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> Assertions.assertThat(response)
                        .contains("\"message\": \"Endpoint en construcción\"")
                        .contains("\"solicitudId\": \"123\""));
    }

    @Test
    void testConsultarPorDocumentoEndpoint() {
        webTestClient.get()
                .uri("/api/v1/solicitud/documento/12345678")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> Assertions.assertThat(response)
                        .contains("\"message\": \"Endpoint en construcción\"")
                        .contains("\"numeroDocumento\": \"12345678\""));
    }
}