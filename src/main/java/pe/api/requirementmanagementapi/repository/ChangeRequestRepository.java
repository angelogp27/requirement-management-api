package pe.api.requirementmanagementapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.ChangeRequest;
import pe.api.requirementmanagementapi.model.enums.EstadoChangeRequest;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, UUID> {
    Page<ChangeRequest> findByRequisitoProyectoId(UUID proyectoId, Pageable pageable);
    List<ChangeRequest> findByRequisitoId(UUID requisitoId);
    Page<ChangeRequest> findByEstado(EstadoChangeRequest estado, Pageable pageable);
}
