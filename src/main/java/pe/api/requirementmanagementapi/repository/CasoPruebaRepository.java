package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.CasoPrueba;

import java.util.List;
import java.util.UUID;

@Repository
public interface CasoPruebaRepository extends JpaRepository<CasoPrueba, UUID> {
    List<CasoPrueba> findByRequisitoId(UUID requisitoId);
}
