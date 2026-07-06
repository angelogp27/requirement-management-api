package pe.api.requirementmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.api.requirementmanagementapi.dto.request.ValidationDecisionRequest;
import pe.api.requirementmanagementapi.dto.request.ValidationSessionCreateRequest;
import pe.api.requirementmanagementapi.dto.response.ValidationSessionReqResponse;
import pe.api.requirementmanagementapi.dto.response.ValidationSessionResponse;
import pe.api.requirementmanagementapi.exception.ResourceNotFoundException;
import pe.api.requirementmanagementapi.model.*;
import pe.api.requirementmanagementapi.repository.CriterioAceptacionRepository;
import pe.api.requirementmanagementapi.repository.ProyectoRepository;
import pe.api.requirementmanagementapi.repository.RequisitoRepository;
import pe.api.requirementmanagementapi.repository.ValidationSessionRepository;
import pe.api.requirementmanagementapi.repository.ValidationSessionReqRepository;
import pe.api.requirementmanagementapi.model.enums.EstadoRequisito;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidationSessionService {

    private final ValidationSessionRepository sessionRepository;
    private final ValidationSessionReqRepository sessionReqRepository;
    private final ProyectoRepository proyectoRepository;
    private final RequisitoRepository requisitoRepository;
    private final CriterioAceptacionRepository criterioRepository;

    @Transactional
    public ValidationSessionResponse createSession(UUID proyectoId, ValidationSessionCreateRequest req) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        // Validar que ningun requisito este en una sesion activa (PENDING, IN_PROGRESS, PAUSED)
        List<ValidationSession> activeSessions = sessionRepository.findByProyectoId(proyectoId).stream()
                .filter(s -> !"COMPLETED".equals(s.getEstado()))
                .collect(Collectors.toList());

        Set<UUID> lockedReqIds = activeSessions.stream()
                .flatMap(s -> s.getRequisitos().stream())
                .map(sr -> sr.getRequisito().getId())
                .collect(Collectors.toSet());

        List<UUID> conflicting = req.getRequisitoIds().stream()
                .filter(lockedReqIds::contains)
                .collect(Collectors.toList());

        if (!conflicting.isEmpty()) {
            throw new IllegalStateException(
                    "Los siguientes requisitos ya estan en una sesion activa o pausada y no pueden incluirse: " +
                    conflicting.stream().map(UUID::toString).collect(Collectors.joining(", ")));
        }

        ValidationSession session = ValidationSession.builder()
                .proyecto(proyecto)
                .titulo("Sesion de Validacion - " + java.time.LocalDate.now().toString())
                .estado("PENDING")
                .build();

        if (req.getCorreosReviewers() != null) {
            List<ValidationSessionReviewer> reviewers = req.getCorreosReviewers().stream()
                    .map(correo -> ValidationSessionReviewer.builder().sesion(session).correo(correo).build())
                    .collect(Collectors.toList());
            session.setReviewers(reviewers);
        }

        List<ValidationSessionReq> reqs = req.getRequisitoIds().stream()
                .map(id -> {
                    Requisito r = requisitoRepository.findById(id).orElseThrow();
                    return ValidationSessionReq.builder().sesion(session).requisito(r).build();
                })
                .collect(Collectors.toList());

        session.setRequisitos(reqs);
        ValidationSession saved = sessionRepository.save(session);
        return toResponse(saved);
    }

    public List<ValidationSessionResponse> listSessions(UUID proyectoId) {
        return sessionRepository.findByProyectoId(proyectoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ValidationSessionResponse getSession(UUID sessionId) {
        return toResponse(sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesion no encontrada")));
    }

    @Transactional
    public ValidationSessionResponse submitDecision(UUID sessionId, UUID requisitoId, ValidationDecisionRequest req) {
        ValidationSessionReq sessionReq = sessionReqRepository.findBySesionIdAndRequisitoId(sessionId, requisitoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito no encontrado en la sesion"));

        sessionReq.setEstadoValidacion(req.getDecision());
        sessionReq.setObservaciones(req.getObservaciones());
        sessionReqRepository.save(sessionReq);

        // Actualizar estado del requisito si fue aprobado
        if ("APPROVED".equals(req.getDecision())) {
            Requisito r = sessionReq.getRequisito();
            r.setEstado(EstadoRequisito.VALIDADO);
            requisitoRepository.save(r);
        }

        ValidationSession session = sessionRepository.findById(sessionId).orElseThrow();
        boolean allDone = session.getRequisitos().stream()
                .noneMatch(r -> "PENDING".equals(r.getEstadoValidacion()));
        if (allDone) {
            session.setEstado("COMPLETED");
        } else {
            session.setEstado("IN_PROGRESS");
        }
        sessionRepository.save(session);

        return toResponse(session);
    }

    @Transactional
    public ValidationSessionResponse pauseSession(UUID sessionId) {
        ValidationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesion no encontrada"));
        session.setEstado("PAUSED");
        sessionRepository.save(session);
        return toResponse(session);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        ValidationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesion no encontrada"));
        sessionRepository.delete(session);
    }

    private ValidationSessionResponse toResponse(ValidationSession session) {
        List<String> reviewers = session.getReviewers().stream()
                .map(ValidationSessionReviewer::getCorreo)
                .collect(Collectors.toList());

        List<ValidationSessionReqResponse> reqResponses = session.getRequisitos().stream()
                .map(sr -> {
                    Requisito r = sr.getRequisito();

                    // Obtener criterios desde la tabla dedicada
                    List<String> criterios = criterioRepository.findByRequisitoId(r.getId()).stream()
                            .map(CriterioAceptacion::getDescripcion)
                            .collect(Collectors.toList());

                    String titulo = r.getCodigo();
                    if (r.getDetallesCasoUso() != null && r.getDetallesCasoUso().containsKey("nombre")) {
                        titulo = (String) r.getDetallesCasoUso().get("nombre");
                    }

                    return ValidationSessionReqResponse.builder()
                            .requisitoId(r.getId())
                            .codigo(r.getCodigo())
                            .titulo(titulo)
                            .descripcion(r.getDescripcion())
                            .criteriosAceptacion(criterios)
                            .estadoValidacion(sr.getEstadoValidacion())
                            .observaciones(sr.getObservaciones())
                            .build();
                })
                .collect(Collectors.toList());

        return ValidationSessionResponse.builder()
                .id(session.getId())
                .proyectoId(session.getProyecto().getId())
                .titulo(session.getTitulo())
                .estado(session.getEstado())
                .fechaCreacion(session.getFechaCreacion())
                .reviewers(reviewers)
                .requisitos(reqResponses)
                .build();
    }
}
