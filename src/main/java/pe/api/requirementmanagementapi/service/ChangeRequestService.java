package pe.api.requirementmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.api.requirementmanagementapi.dto.request.ChangeRequestRequest;
import pe.api.requirementmanagementapi.dto.response.ChangeRequestResponse;
import pe.api.requirementmanagementapi.exception.ResourceNotFoundException;
import pe.api.requirementmanagementapi.model.ChangeRequest;
import pe.api.requirementmanagementapi.model.Requisito;
import pe.api.requirementmanagementapi.model.Usuario;
import pe.api.requirementmanagementapi.model.enums.EstadoChangeRequest;
import pe.api.requirementmanagementapi.repository.ChangeRequestRepository;
import pe.api.requirementmanagementapi.repository.RequisitoRepository;
import pe.api.requirementmanagementapi.repository.UsuarioRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final RequisitoRepository requisitoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BayesianInferenceService bayesianInferenceService;

    @Transactional(readOnly = true)
    public Page<ChangeRequestResponse> listarPorProyecto(UUID proyectoId, Pageable pageable) {
        return changeRequestRepository.findByRequisitoProyectoId(proyectoId, pageable)
                .map(ChangeRequestResponse::fromEntity);
    }

    @Transactional
    public ChangeRequestResponse crearChangeRequest(UUID requisitoId, ChangeRequestRequest request) {
        Requisito requisito = requisitoRepository.findById(requisitoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito no encontrado"));
        Usuario solicitante = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Solicitante no encontrado"));

        ChangeRequest cr = ChangeRequest.builder()
                .requisito(requisito)
                .solicitante(solicitante)
                .justificacion(request.getJustificacion())
                .impactoTecnico(request.getImpactoTecnico())
                .impactoNegocio(request.getImpactoNegocio())
                .riesgos(request.getRiesgos())
                .esfuerzoEstimado(request.getEsfuerzoEstimado())
                .estado(EstadoChangeRequest.PROPUESTO)
                .build();

        // Inferencia Bayesiana: calcular riesgo automaticamente
        double riesgo = bayesianInferenceService.calcularRiesgo(requisito);
        cr.setProbabilidadRiesgo(java.math.BigDecimal.valueOf(riesgo));

        return ChangeRequestResponse.fromEntity(changeRequestRepository.save(cr));
    }

    @Transactional
    public ChangeRequestResponse analizarChangeRequest(UUID id, UUID revisorId) {
        ChangeRequest cr = changeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChangeRequest no encontrado"));
        Usuario revisor = usuarioRepository.findById(revisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Revisor no encontrado"));

        cr.setEstado(EstadoChangeRequest.ANALIZADO);
        cr.setRevisadoPor(revisor);
        return ChangeRequestResponse.fromEntity(changeRequestRepository.save(cr));
    }

    @Transactional
    public ChangeRequestResponse aprobarChangeRequest(UUID id, UUID revisorId) {
        ChangeRequest cr = changeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChangeRequest no encontrado"));
        Usuario revisor = usuarioRepository.findById(revisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Revisor no encontrado"));

        cr.setEstado(EstadoChangeRequest.APROBADO);
        cr.setRevisadoPor(revisor);

        // TODO: Crear RequisitoVersion y aplicar cambios al Requisito si es necesario.
        return ChangeRequestResponse.fromEntity(changeRequestRepository.save(cr));
    }

    @Transactional
    public ChangeRequestResponse rechazarChangeRequest(UUID id, UUID revisorId) {
        ChangeRequest cr = changeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChangeRequest no encontrado"));
        Usuario revisor = usuarioRepository.findById(revisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Revisor no encontrado"));

        cr.setEstado(EstadoChangeRequest.RECHAZADO);
        cr.setRevisadoPor(revisor);
        return ChangeRequestResponse.fromEntity(changeRequestRepository.save(cr));
    }
}
