package pe.api.requirementmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.api.requirementmanagementapi.dto.request.ChecklistValidacionRequest;
import pe.api.requirementmanagementapi.dto.request.ComentarioReviewRequest;
import pe.api.requirementmanagementapi.dto.response.ApiResponse;
import pe.api.requirementmanagementapi.dto.response.ChecklistValidacionResponse;
import pe.api.requirementmanagementapi.dto.response.ComentarioReviewResponse;
import pe.api.requirementmanagementapi.service.ValidacionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/requirements/{requisitoId}/validation")
@RequiredArgsConstructor
public class ValidacionController {

    private final ValidacionService validacionService;

    // --- Peer Review ---

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<ComentarioReviewResponse>>> listarComentarios(
            @PathVariable UUID requisitoId) {
        return ResponseEntity.ok(ApiResponse.ok(validacionService.listarComentarios(requisitoId)));
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<ComentarioReviewResponse>> agregarComentario(
            @PathVariable UUID requisitoId,
            @Valid @RequestBody ComentarioReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok("Comentario agregado", validacionService.agregarComentario(requisitoId, request)));
    }

    // --- Checklist ---

    @GetMapping("/checklist")
    public ResponseEntity<ApiResponse<ChecklistValidacionResponse>> obtenerChecklist(
            @PathVariable UUID requisitoId,
            @RequestParam UUID evaluadorId) {
        ChecklistValidacionResponse response = validacionService.obtenerChecklist(requisitoId, evaluadorId);
        return ResponseEntity.ok(ApiResponse.ok(response)); // Puede retornar null si no hay datos
    }

    @PutMapping("/checklist")
    public ResponseEntity<ApiResponse<ChecklistValidacionResponse>> guardarChecklist(
            @PathVariable UUID requisitoId,
            @Valid @RequestBody ChecklistValidacionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Checklist guardado", validacionService.guardarChecklist(requisitoId, request)));
    }
}
