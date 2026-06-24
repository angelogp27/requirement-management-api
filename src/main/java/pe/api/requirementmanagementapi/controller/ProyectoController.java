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
import pe.api.requirementmanagementapi.dto.request.ProyectoRequest;
import pe.api.requirementmanagementapi.dto.response.ApiResponse;
import pe.api.requirementmanagementapi.dto.response.ProyectoResponse;
import pe.api.requirementmanagementapi.dto.response.RequisitoResponse;
import pe.api.requirementmanagementapi.service.ProyectoService;
import pe.api.requirementmanagementapi.service.RequisitoService;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de proyectos.
 * Endpoints según doc backend sección 5.2.
 * Base URL: /api/projects
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final RequisitoService requisitoService;

    /**
     * POST /api/projects - Crear un nuevo proyecto (RFB-006).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProyectoResponse>> crearProyecto(
            @Valid @RequestBody ProyectoRequest request) {
        ProyectoResponse response = proyectoService.crearProyecto(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Proyecto creado exitosamente", response));
    }

    /**
     * GET /api/projects - Listar proyectos con paginación (RFB-007).
     * Parámetros: page (default 0), size (default 20).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProyectoResponse>>> listarProyectos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        Page<ProyectoResponse> response = proyectoService.listarProyectos(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * GET /api/projects/{id} - Obtener proyecto por ID (RFB-008).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProyectoResponse>> obtenerProyecto(@PathVariable UUID id) {
        ProyectoResponse response = proyectoService.obtenerProyecto(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * PUT /api/projects/{id} - Actualizar proyecto (RFB-009).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProyectoResponse>> actualizarProyecto(
            @PathVariable UUID id,
            @Valid @RequestBody ProyectoRequest request) {
        ProyectoResponse response = proyectoService.actualizarProyecto(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Proyecto actualizado exitosamente", response));
    }

    /**
     * DELETE /api/projects/{id} - Soft delete (marca como CERRADO) (RFB-010).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarProyecto(@PathVariable UUID id) {
        proyectoService.eliminarProyecto(id);
        return ResponseEntity.ok(ApiResponse.ok("Proyecto eliminado exitosamente", null));
    }

    /**
     * GET /api/projects/{id}/traceability - Matriz de trazabilidad (RFB-023).
     * Retorna todos los requisitos del proyecto con sus asignaciones.
     */
    @GetMapping("/{id}/traceability")
    public ResponseEntity<ApiResponse<List<RequisitoResponse>>> obtenerTrazabilidad(
            @PathVariable UUID id) {
        List<RequisitoResponse> response = requisitoService.obtenerTrazabilidad(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * POST /api/projects/{id}/export/ers - Generar documento DOCX ERS (RFB-032).
     */
    @PostMapping("/{id}/export/ers")
    public ResponseEntity<byte[]> exportarERS(@PathVariable UUID id) {
        // Generamos un documento de ejemplo para la fase actual.
        // TODO: Implementar integración real con docx4j usando el servicio.
        String content = "Especificación de Requisitos de Software (ERS) - Proyecto " + id;
        byte[] docBytes = content.getBytes();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=REM_" + id + ".docx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        return new ResponseEntity<>(docBytes, headers, HttpStatus.OK);
    }
}
