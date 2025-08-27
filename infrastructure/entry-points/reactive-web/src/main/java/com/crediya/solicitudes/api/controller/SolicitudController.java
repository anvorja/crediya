// infrastructure/entry-points/reactive-web/src/main/java/com/crediya/solicitudes/api/controller/SolicitudController.java
package com.crediya.solicitudes.api.controller;

import com.crediya.solicitudes.api.dto.request.CrearSolicitudRequest;
import com.crediya.solicitudes.api.dto.response.SolicitudResponse;
import com.crediya.solicitudes.api.mapper.SolicitudRestMapper;
import com.crediya.solicitudes.model.solicitud.Solicitud;
import com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de solicitudes de préstamo
 * Entry Point - Capa de infraestructura que expone los casos de uso
 */
@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
@Slf4j
public class SolicitudController {

    private final CrearSolicitudUseCase crearSolicitudUseCase;
    private final SolicitudRestMapper mapper;

    /**
     * HU: Como cliente, quiero enviar mi solicitud de préstamo
     * con la información necesaria para que CrediYa pueda evaluarla
     * POST /api/v1/solicitudes
     */
    @PostMapping
    public ResponseEntity<SolicitudResponse> crearSolicitud(
            @Valid @RequestBody CrearSolicitudRequest request) {

        try {
            log.info("🚀 Iniciando creación de solicitud para documento: {}", request.getNumeroDocumento());

            // 1. Mapear DTO → Entidad de Dominio
            Solicitud solicitudDominio = mapper.toModel(request);

            // 2. Ejecutar Caso de Uso PURO (sin Reactor)
            Solicitud solicitudCreada = crearSolicitudUseCase.ejecutar(solicitudDominio);

            // 3. Mapear Entidad de Dominio → DTO Response
            SolicitudResponse response = mapper.toResponse(solicitudCreada);

            log.info("✅ Solicitud creada exitosamente: {} - Estado: {}",
                    solicitudCreada.getId(), solicitudCreada.getEstado());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("❌ Error creando solicitud para documento {}: {}",
                    request.getNumeroDocumento(), e.getMessage(), e);
            throw e; // Se manejará por el GlobalExceptionHandler
        }
    }

    /**
     * GET /api/v1/solicitudes/{id}
     * Consultar una solicitud específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> consultarSolicitud(@PathVariable String id) {
        log.info("🔍 Consultando solicitud: {}", id);

        // TODO: Implementar ConsultarSolicitudUseCase
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/solicitudes/documento/{numeroDocumento}
     * Consultar solicitud por número de documento
     */
    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<SolicitudResponse> consultarPorDocumento(@PathVariable String numeroDocumento) {
        log.info("🔍 Consultando solicitud por documento: {}", numeroDocumento);

        // TODO: Implementar ConsultarSolicitudPorDocumentoUseCase
        return ResponseEntity.ok().build();
    }
}