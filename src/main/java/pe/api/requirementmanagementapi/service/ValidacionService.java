package pe.api.requirementmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.api.requirementmanagementapi.dto.request.ChecklistValidacionRequest;
import pe.api.requirementmanagementapi.dto.request.ComentarioReviewRequest;
import pe.api.requirementmanagementapi.dto.response.ChecklistValidacionResponse;
import pe.api.requirementmanagementapi.dto.response.ComentarioReviewResponse;
import pe.api.requirementmanagementapi.exception.ResourceNotFoundException;
import pe.api.requirementmanagementapi.model.ChecklistValidacion;
import pe.api.requirementmanagementapi.model.ComentarioReview;
import pe.api.requirementmanagementapi.model.Requisito;
import pe.api.requirementmanagementapi.model.Usuario;
import pe.api.requirementmanagementapi.repository.ChecklistValidacionRepository;
import pe.api.requirementmanagementapi.repository.ComentarioReviewRepository;
import pe.api.requirementmanagementapi.repository.RequisitoRepository;
import pe.api.requirementmanagementapi.repository.UsuarioRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidacionService {

    private final ChecklistValidacionRepository checklistRepository;
    private final ComentarioReviewRepository comentarioRepository;
    private final RequisitoRepository requisitoRepository;
    private final UsuarioRepository usuarioRepository;

    // --- Peer Review (Comentarios) ---

    @Transactional(readOnly = true)
    public List<ComentarioReviewResponse> listarComentarios(UUID requisitoId) {
        return comentarioRepository.findByRequisitoIdOrderByFechaCreacionAsc(requisitoId)
                .stream()
                .map(ComentarioReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComentarioReviewResponse agregarComentario(UUID requisitoId, ComentarioReviewRequest request) {
        Requisito requisito = requisitoRepository.findById(requisitoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito no encontrado"));
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ComentarioReview comentario = ComentarioReview.builder()
                .requisito(requisito)
                .usuario(usuario)
                .contenido(request.getContenido())
                .build();

        return ComentarioReviewResponse.fromEntity(comentarioRepository.save(comentario));
    }

    // --- Checklist Validación ---

    @Transactional(readOnly = true)
    public ChecklistValidacionResponse obtenerChecklist(UUID requisitoId, UUID evaluadorId) {
        return checklistRepository.findByRequisitoIdAndEvaluadorId(requisitoId, evaluadorId)
                .map(ChecklistValidacionResponse::fromEntity)
                .orElse(null); // Retorna null si no existe para que el frontend pueda crearlo
    }

    @Transactional
    public ChecklistValidacionResponse guardarChecklist(UUID requisitoId, ChecklistValidacionRequest request) {
        Requisito requisito = requisitoRepository.findById(requisitoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito no encontrado"));
        Usuario evaluador = usuarioRepository.findById(request.getEvaluadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluador no encontrado"));

        ChecklistValidacion checklist = checklistRepository.findByRequisitoIdAndEvaluadorId(requisitoId, evaluador.getId())
                .orElse(ChecklistValidacion.builder()
                        .requisito(requisito)
                        .evaluador(evaluador)
                        .build());

        checklist.setEsAtomico(request.isEsAtomico());
        checklist.setEsTestable(request.isEsTestable());
        checklist.setEsFactible(request.isEsFactible());
        checklist.setNoAmbiguo(request.isNoAmbiguo());
        checklist.setEsCompleto(request.isEsCompleto());
        checklist.setObservaciones(request.getObservaciones());

        return ChecklistValidacionResponse.fromEntity(checklistRepository.save(checklist));
    }
}
