package pe.api.requirementmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.api.requirementmanagementapi.dto.response.DashboardStatsResponse;
import pe.api.requirementmanagementapi.dto.response.ProjectStatsDto;
import pe.api.requirementmanagementapi.model.Proyecto;
import pe.api.requirementmanagementapi.model.enums.EstadoProyecto;
import pe.api.requirementmanagementapi.model.enums.EstadoRequisito;
import pe.api.requirementmanagementapi.repository.ProyectoRepository;
import pe.api.requirementmanagementapi.repository.RequisitoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProyectoRepository proyectoRepository;
    private final RequisitoRepository requisitoRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalProjects = proyectoRepository.count();
        long totalRequirements = requisitoRepository.count();
        long approvedRequirements = requisitoRepository.countByEstado(EstadoRequisito.APROBADO);
        long pendingRequirements = requisitoRepository.countByEstado(EstadoRequisito.REGISTRADO) +
                                   requisitoRepository.countByEstado(EstadoRequisito.EN_ANALISIS) +
                                   requisitoRepository.countByEstado(EstadoRequisito.VALIDADO);

        List<Proyecto> activeProjects = proyectoRepository.findAll().stream()
                .filter(p -> p.getEstado() != EstadoProyecto.CERRADO)
                .toList();

        List<ProjectStatsDto> projects = new ArrayList<>();
        for (Proyecto p : activeProjects) {
            long pTotal = requisitoRepository.countByProyectoId(p.getId());
            long pApproved = requisitoRepository.countByProyectoIdAndEstado(p.getId(), EstadoRequisito.APROBADO);
            long pPending = requisitoRepository.countByProyectoIdAndEstado(p.getId(), EstadoRequisito.REGISTRADO) +
                            requisitoRepository.countByProyectoIdAndEstado(p.getId(), EstadoRequisito.EN_ANALISIS) +
                            requisitoRepository.countByProyectoIdAndEstado(p.getId(), EstadoRequisito.VALIDADO);

            projects.add(ProjectStatsDto.builder()
                    .id(p.getId())
                    .codigo(p.getCodigo())
                    .nombre(p.getNombre())
                    .totalRequirements(pTotal)
                    .approvedRequirements(pApproved)
                    .pendingRequirements(pPending)
                    .build());
        }

        return DashboardStatsResponse.builder()
                .totalProjects(totalProjects)
                .totalRequirements(totalRequirements)
                .approvedRequirements(approvedRequirements)
                .pendingRequirements(pendingRequirements)
                .projects(projects)
                .build();
    }
}
