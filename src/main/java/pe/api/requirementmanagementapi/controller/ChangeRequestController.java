package pe.api.requirementmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.api.requirementmanagementapi.dto.request.ChangeRequestRequest;
import pe.api.requirementmanagementapi.dto.response.ApiResponse;
import pe.api.requirementmanagementapi.dto.response.ChangeRequestResponse;
import pe.api.requirementmanagementapi.service.ChangeRequestService;

import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/change-requests")
@RequiredArgsConstructor
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ChangeRequestResponse>>> listar(
            @PathVariable UUID projectId,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(changeRequestService.listarPorProyecto(projectId, pageable)));
    }

    @PostMapping("/requirements/{requisitoId}")
    public ResponseEntity<ApiResponse<ChangeRequestResponse>> crear(
            @PathVariable UUID projectId,
            @PathVariable UUID requisitoId,
            @Valid @RequestBody ChangeRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok("Solicitud de cambio creada", changeRequestService.crearChangeRequest(requisitoId, request)));
    }

    @PutMapping("/{id}/analyze")
    public ResponseEntity<ApiResponse<ChangeRequestResponse>> analizar(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestParam UUID revisorId) {
        return ResponseEntity.ok(ApiResponse.ok("Solicitud analizada", changeRequestService.analizarChangeRequest(id, revisorId)));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ChangeRequestResponse>> aprobar(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestParam UUID revisorId) {
        return ResponseEntity.ok(ApiResponse.ok("Solicitud aprobada", changeRequestService.aprobarChangeRequest(id, revisorId)));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ChangeRequestResponse>> rechazar(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestParam UUID revisorId) {
        return ResponseEntity.ok(ApiResponse.ok("Solicitud rechazada", changeRequestService.rechazarChangeRequest(id, revisorId)));
    }
}
