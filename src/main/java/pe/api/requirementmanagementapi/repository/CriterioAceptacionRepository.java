package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.CriterioAceptacion;

import java.util.List;
import java.util.UUID;

@Repository
public interface CriterioAceptacionRepository extends JpaRepository<CriterioAceptacion, UUID> {
    List<CriterioAceptacion> findByRequisitoId(UUID requisitoId);
}
