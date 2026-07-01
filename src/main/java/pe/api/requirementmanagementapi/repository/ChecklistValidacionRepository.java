package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.ChecklistValidacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistValidacionRepository extends JpaRepository<ChecklistValidacion, UUID> {
    List<ChecklistValidacion> findByRequisitoId(UUID requisitoId);
    Optional<ChecklistValidacion> findByRequisitoIdAndEvaluadorId(UUID requisitoId, UUID evaluadorId);
}
