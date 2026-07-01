package pe.api.requirementmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.api.requirementmanagementapi.dto.request.StakeholderRequest;
import pe.api.requirementmanagementapi.dto.response.ApiResponse;
import pe.api.requirementmanagementapi.dto.response.StakeholderResponse;
import pe.api.requirementmanagementapi.exception.ResourceNotFoundException;
import pe.api.requirementmanagementapi.model.Proyecto;
import pe.api.requirementmanagementapi.model.Stakeholder;
import pe.api.requirementmanagementapi.repository.ProyectoRepository;
import pe.api.requirementmanagementapi.repository.StakeholderRepository;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de Stakeholders.
 * Base URL: /api/projects/{projectId}/stakeholders
 * Soporta: Módulo de Captura.
 */
@RestController
@RequestMapping("/projects/{projectId}/stakeholders")
@RequiredArgsConstructor
public class StakeholderController {

    private final StakeholderRepository stakeholderRepository;
    private final ProyectoRepository proyectoRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StakeholderResponse>>> listar(
            @PathVariable UUID projectId) {
        List<StakeholderResponse> list = stakeholderRepository.findByProyectoId(projectId)
                .stream()
                .map(StakeholderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StakeholderResponse>> crear(
            @PathVariable UUID projectId,
            @Valid @RequestBody StakeholderRequest request) {
        Proyecto proyecto = proyectoRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        Stakeholder stakeholder = Stakeholder.builder()
                .proyecto(proyecto)
                .nombre(request.getNombre())
                .rol(request.getRol())
                .organizacion(request.getOrganizacion())
                .nivelInfluencia(request.getNivelInfluencia() != null ? request.getNivelInfluencia() : "MEDIO")
                .contacto(request.getContacto())
                .build();

        stakeholder = stakeholderRepository.save(stakeholder);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Stakeholder creado exitosamente", StakeholderResponse.fromEntity(stakeholder)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StakeholderResponse>> actualizar(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody StakeholderRequest request) {
        Stakeholder stakeholder = stakeholderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stakeholder no encontrado"));

        stakeholder.setNombre(request.getNombre());
        stakeholder.setRol(request.getRol());
        stakeholder.setOrganizacion(request.getOrganizacion());
        if (request.getNivelInfluencia() != null) {
            stakeholder.setNivelInfluencia(request.getNivelInfluencia());
        }
        stakeholder.setContacto(request.getContacto());

        stakeholder = stakeholderRepository.save(stakeholder);
        return ResponseEntity.ok(ApiResponse.ok("Stakeholder actualizado", StakeholderResponse.fromEntity(stakeholder)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        stakeholderRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Stakeholder eliminado", null));
    }
}
