// infrastructure/entry-points/reactive-web/src/main/java/com/crediya/solicitudes/api/Handler.java
package com.crediya.solicitudes.api;

import com.crediya.solicitudes.api.dto.request.CrearSolicitudRequest;
import com.crediya.solicitudes.api.dto.response.SolicitudResponse;
import com.crediya.solicitudes.api.mapper.SolicitudRestMapper;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler para Router Functions (estilo funcional de WebFlux)
 * Integramos el dominio puro con la infraestructura reactiva
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final CrearSolicitudUseCase crearSolicitudUseCase;
    private final SolicitudRestMapper mapper;

    /**
     * POST /api/v1/solicitud
     * Handler para crear solicitud de préstamo
     */
    public Mono<ServerResponse> crearSolicitud(ServerRequest request) {
        log.info("Handler - Procesando creación de solicitud");

        return request.bodyToMono(CrearSolicitudRequest.class)
                .flatMap(this::procesarSolicitud)
                .flatMap(response -> ServerResponse.status(201) // HTTP 201 Created
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    /**
     * GET /api/v1/solicitud/{id}
     * Handler para consultar solicitud por ID
     */
    public Mono<ServerResponse> consultarSolicitud(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("🔍 Handler - Consultando solicitud: {}", id);

        // TODO: Implementar cuando tengamos ConsultarSolicitudUseCase
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"message\": \"Endpoint en construcción\", \"solicitudId\": \"" + id + "\" }");
    }

    /**
     * GET /api/v1/solicitud/documento/{numeroDocumento}
     * Handler para consultar solicitud por número de documento
     */
    public Mono<ServerResponse> consultarPorDocumento(ServerRequest request) {
        String numeroDocumento = request.pathVariable("numeroDocumento");
        log.info("🔍 Handler - Consultando solicitud por documento: {}", numeroDocumento);

        // TODO: Implementar cuando tengamos ConsultarSolicitudPorDocumentoUseCase
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"message\": \"Endpoint en construcción\", \"numeroDocumento\": \"" + numeroDocumento + "\" }");
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    /**
     * Procesa la creación de solicitud - CONVIERTE REACTIVO → SÍNCRONO → REACTIVO
     */
    private Mono<SolicitudResponse> procesarSolicitud(CrearSolicitudRequest request) {
        return Mono.fromCallable(() -> {
            log.info("Procesando solicitud para documento: {}", request.getNumeroDocumento());

            // 1. Mapear DTO → Entidad de Dominio
            Solicitud solicitudDominio = mapper.toModel(request);

            // 2. Ejecutar Caso de Uso PURO (síncrono - sin Reactor)
            Solicitud solicitudCreada = crearSolicitudUseCase.ejecutar(solicitudDominio);

            // 3. Mapear Entidad de Dominio → DTO Response
            SolicitudResponse response = mapper.toResponse(solicitudCreada);

            log.info("Solicitud creada exitosamente: {} - Estado: {}",
                    solicitudCreada.getId(), solicitudCreada.getEstado());

            return response;

            // NO ENVOLVER EN RuntimeException - dejar que las excepciones del dominio se propaguen
        });
    }
}