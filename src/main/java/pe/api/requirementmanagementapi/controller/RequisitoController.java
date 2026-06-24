package pe.api.requirementmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.api.requirementmanagementapi.dto.request.RequisitoRequest;
import pe.api.requirementmanagementapi.dto.response.ApiResponse;
import pe.api.requirementmanagementapi.dto.response.HistorialResponse;
import pe.api.requirementmanagementapi.dto.response.RequisitoResponse;
import pe.api.requirementmanagementapi.model.enums.EstadoRequisito;
import pe.api.requirementmanagementapi.service.RequisitoHistorialService;
import pe.api.requirementmanagementapi.service.RequisitoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador REST para la gestión de requisitos.
 * Endpoints según doc backend secciones 5.3 y 5.4.
 * Base URL: /api/projects/{projectId}/requirements
 *
 * Nota: Hasta que se implemente JWT, el ID del usuario autor
 * se pasa mediante el header 'X-Usuario-Id'.
 */
@RestController
@RequestMapping("/projects/{projectId}/requirements")
@RequiredArgsConstructor
public class RequisitoController {

    private final RequisitoService requisitoService;
    private final RequisitoHistorialService historialService;

    /**
     * POST /api/projects/{projectId}/requirements
     * Crear un nuevo requisito con código autogenerado (RFB-013).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RequisitoResponse>> crearRequisito(
            @PathVariable UUID projectId,
            @Valid @RequestBody RequisitoRequest request,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        RequisitoResponse response = requisitoService.crearRequisito(projectId, request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Requisito creado exitosamente", response));
    }

    /**
     * GET /api/projects/{projectId}/requirements
     * Listar requisitos con filtros opcionales y paginación (RFB-014, RFB-015).
     * Filtros: estado, prioridad, tipo.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RequisitoResponse>>> listarRequisitos(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        Page<RequisitoResponse> response = requisitoService.listarRequisitos(
                projectId, estado, prioridad, tipo, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * GET /api/projects/{projectId}/requirements/{id}
     * Obtener detalles completos de un requisito (RFB-016).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RequisitoResponse>> obtenerRequisito(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        RequisitoResponse response = requisitoService.obtenerRequisito(projectId, id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * PUT /api/projects/{projectId}/requirements/{id}
     * Actualizar requisito con registro automático de historial (RFB-017).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RequisitoResponse>> actualizarRequisito(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody RequisitoRequest request,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        RequisitoResponse response = requisitoService.actualizarRequisito(
                projectId, id, request, usuarioId);
        return ResponseEntity.ok(ApiResponse.ok("Requisito actualizado exitosamente", response));
    }

    /**
     * DELETE /api/projects/{projectId}/requirements/{id}
     * Eliminar requisito (RFB-018).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarRequisito(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        requisitoService.eliminarRequisito(projectId, id, usuarioId);
        return ResponseEntity.ok(ApiResponse.ok("Requisito eliminado exitosamente", null));
    }

    /**
     * PATCH /api/projects/{projectId}/requirements/{id}/estado
     * Cambiar estado del requisito (flujo de estados).
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<RequisitoResponse>> cambiarEstado(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        EstadoRequisito nuevoEstado = EstadoRequisito.valueOf(body.get("estado"));
        RequisitoResponse response = requisitoService.cambiarEstado(projectId, id, nuevoEstado, usuarioId);
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado exitosamente", response));
    }

    /**
     * POST /api/projects/{projectId}/requirements/{id}/assign
     * Asignar requisito a usuario técnico (RFB-022).
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<RequisitoResponse>> asignarUsuario(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestBody Map<String, UUID> body,
            @RequestHeader("X-Usuario-Id") UUID usuarioId) {
        UUID asignadoId = body.get("userId");
        RequisitoResponse response = requisitoService.asignarUsuario(projectId, id, asignadoId, usuarioId);
        return ResponseEntity.ok(ApiResponse.ok("Requisito asignado exitosamente", response));
    }

    /**
     * GET /api/projects/{projectId}/requirements/{id}/history
     * Obtener historial de cambios de un requisito (RFB-028).
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<Page<HistorialResponse>>> obtenerHistorial(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HistorialResponse> response = historialService.obtenerHistorial(projectId, id, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * POST /api/projects/{projectId}/requirements/{id}/dependencias
     * Agregar dependencia entre requisitos.
     */
    @PostMapping("/{id}/dependencias")
    public ResponseEntity<ApiResponse<RequisitoResponse>> agregarDependencia(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestBody Map<String, UUID> body) {
        UUID dependenciaId = body.get("dependenciaId");
        RequisitoResponse response = requisitoService.agregarDependencia(projectId, id, dependenciaId);
        return ResponseEntity.ok(ApiResponse.ok("Dependencia agregada exitosamente", response));
    }

    /**
     * GET /api/projects/{projectId}/requirements/{id}/dependencias
     * Obtener dependencias de un requisito.
     */
    @GetMapping("/{id}/dependencias")
    public ResponseEntity<ApiResponse<List<RequisitoResponse>>> obtenerDependencias(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        List<RequisitoResponse> response = requisitoService.obtenerDependencias(projectId, id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
