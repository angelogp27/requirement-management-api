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
import pe.api.requirementmanagementapi.repository.RequisitoVersionRepository;
import pe.api.requirementmanagementapi.repository.UsuarioRepository;
import pe.api.requirementmanagementapi.repository.CasoPruebaRepository;
import pe.api.requirementmanagementapi.repository.ObjetivoNegocioRepository;
import pe.api.requirementmanagementapi.model.RequisitoVersion;
import pe.api.requirementmanagementapi.model.CasoPrueba;
import pe.api.requirementmanagementapi.model.ObjetivoNegocio;
import pe.api.requirementmanagementapi.dto.response.ImpactMatrixResponse;
import pe.api.requirementmanagementapi.dto.response.ImpactMatrixItem;
import pe.api.requirementmanagementapi.dto.response.RequisitoVersionResponse;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final RequisitoRepository requisitoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BayesianInferenceService bayesianInferenceService;
    private final RequisitoVersionRepository versionRepository;
    private final CasoPruebaRepository casoPruebaRepository;
    private final ObjetivoNegocioRepository objetivoNegocioRepository;

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
                .textoPropuesto(request.getTextoPropuesto())
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
        cr.setFechaResolucion(java.time.LocalDateTime.now());

        // Snapshot JSON of current requirement state before update and the proposed text
        String textoAnterior = cr.getRequisito().getDescripcion() != null ? cr.getRequisito().getDescripcion().replace("\"", "\\\"").replace("\n", "\\n") : "";
        String textoNuevo = cr.getTextoPropuesto() != null ? cr.getTextoPropuesto().replace("\"", "\\\"").replace("\n", "\\n") : "";
        String snapshot = "{\"texto_anterior\": \"" + textoAnterior + "\", \"texto_nuevo\": \"" + textoNuevo + "\"}";
        
        // Find latest version number
        List<RequisitoVersion> versions = versionRepository.findByRequisitoId(cr.getRequisito().getId());
        int latestVer = versions.size() + 1;
        String newVersionStr = "v" + latestVer + ".0";

        RequisitoVersion version = RequisitoVersion.builder()
                .requisito(cr.getRequisito())
                .version(newVersionStr)
                .snapshotJson(snapshot)
                .changeRequest(cr)
                .creadoPor(revisor)
                .build();
        versionRepository.save(version);

        // Apply changes to requirement
        Requisito req = cr.getRequisito();
        req.setDescripcion(cr.getTextoPropuesto());
        requisitoRepository.save(req);

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
        cr.setFechaResolucion(java.time.LocalDateTime.now());
        
        // Find latest version number
        List<RequisitoVersion> versions = versionRepository.findByRequisitoId(cr.getRequisito().getId());
        int latestVer = versions.size() + 1;
        String newVersionStr = "v1." + latestVer;

        String textoAnterior = cr.getRequisito().getDescripcion() != null ? cr.getRequisito().getDescripcion().replace("\"", "\\\"").replace("\n", "\\n") : "";
        String textoNuevo = cr.getTextoPropuesto() != null ? cr.getTextoPropuesto().replace("\"", "\\\"").replace("\n", "\\n") : "";
        String snapshot = "{\"rechazado\": true, \"texto_anterior\": \"" + textoAnterior + "\", \"texto_nuevo\": \"" + textoNuevo + "\"}";

        RequisitoVersion version = RequisitoVersion.builder()
                .requisito(cr.getRequisito())
                .version(newVersionStr)
                .snapshotJson(snapshot)
                .changeRequest(cr)
                .creadoPor(revisor)
                .build();
        versionRepository.save(version);

        return ChangeRequestResponse.fromEntity(changeRequestRepository.save(cr));
    }

    @Transactional(readOnly = true)
    public ImpactMatrixResponse getImpactMatrix(UUID requisitoId) {
        Requisito req = requisitoRepository.findById(requisitoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito no encontrado"));
                
        double riesgo = bayesianInferenceService.calcularRiesgo(req);
        String nivelRiesgo = riesgo > 70 ? "CRITICO" : (riesgo > 40 ? "ALTO" : (riesgo > 20 ? "MEDIO" : "BAJO"));
        
        List<ImpactMatrixItem> items = new ArrayList<>();
        
        // Mock Casos de Prueba
        List<CasoPrueba> tcList = casoPruebaRepository.findByRequisitoId(requisitoId);
        for (CasoPrueba tc : tcList) {
            items.add(ImpactMatrixItem.builder()
                    .elementoId(tc.getCodigo())
                    .titulo(tc.getTitulo())
                    .tipoElemento("Caso de Prueba")
                    .tipoRelacion("Cobertura")
                    .nivelImpacto(nivelRiesgo)
                    .esfuerzoEstimado("4 hrs.")
                    .build());
        }
        
        // Mock Objetivos
        List<ObjetivoNegocio> objList = objetivoNegocioRepository.findByRequisitoId(requisitoId);
        for (ObjetivoNegocio obj : objList) {
            items.add(ImpactMatrixItem.builder()
                    .elementoId(obj.getCodigo())
                    .titulo(obj.getDescripcion())
                    .tipoElemento("Objetivo de Negocio")
                    .tipoRelacion("Trazabilidad")
                    .nivelImpacto(riesgo > 50 ? "ALTO" : "BAJO")
                    .esfuerzoEstimado("0 hrs.")
                    .build());
        }

        // Mock Dependiente (Child Requirements could be queried from dependencias)
        
        return ImpactMatrixResponse.builder()
                .riesgoProbabilidad(riesgo)
                .nivelRiesgoGeneral(nivelRiesgo)
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RequisitoVersionResponse> getRequirementHistory(UUID requisitoId) {
        return versionRepository.findByRequisitoId(requisitoId).stream()
                .map(RequisitoVersionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
