package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.Stakeholder;

import java.util.List;
import java.util.UUID;

@Repository
public interface StakeholderRepository extends JpaRepository<Stakeholder, UUID> {
    List<Stakeholder> findByProyectoId(UUID proyectoId);
}
