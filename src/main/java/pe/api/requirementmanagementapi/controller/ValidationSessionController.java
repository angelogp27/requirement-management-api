package pe.api.requirementmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.api.requirementmanagementapi.dto.request.ValidationDecisionRequest;
import pe.api.requirementmanagementapi.dto.request.ValidationSessionCreateRequest;
import pe.api.requirementmanagementapi.dto.response.ApiResponse;
import pe.api.requirementmanagementapi.dto.response.ValidationSessionResponse;
import pe.api.requirementmanagementapi.service.ValidationSessionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ValidationSessionController {

    private final ValidationSessionService sessionService;

    @PostMapping("/projects/{projectId}/validation-sessions")
    public ResponseEntity<ApiResponse<ValidationSessionResponse>> createSession(
            @PathVariable UUID projectId,
            @Valid @RequestBody ValidationSessionCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Sesion de validacion creada",
                sessionService.createSession(projectId, request)
        ));
    }

    @GetMapping("/projects/{projectId}/validation-sessions")
    public ResponseEntity<ApiResponse<List<ValidationSessionResponse>>> listSessions(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.listSessions(projectId)
        ));
    }

    @GetMapping("/validation-sessions/{sessionId}")
    public ResponseEntity<ApiResponse<ValidationSessionResponse>> getSession(
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.getSession(sessionId)
        ));
    }

    @PutMapping("/validation-sessions/{sessionId}/requirements/{reqId}/decision")
    public ResponseEntity<ApiResponse<ValidationSessionResponse>> submitDecision(
            @PathVariable UUID sessionId,
            @PathVariable UUID reqId,
            @Valid @RequestBody ValidationDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Decision registrada correctamente",
                sessionService.submitDecision(sessionId, reqId, request)
        ));
    }

    @PutMapping("/validation-sessions/{sessionId}/pause")
    public ResponseEntity<ApiResponse<ValidationSessionResponse>> pauseSession(
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Sesion pausada",
                sessionService.pauseSession(sessionId)
        ));
    }

    @DeleteMapping("/validation-sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @PathVariable UUID sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok(ApiResponse.ok("Sesion eliminada", null));
    }
}
