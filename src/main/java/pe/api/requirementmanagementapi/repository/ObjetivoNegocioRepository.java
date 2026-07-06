package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.ObjetivoNegocio;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObjetivoNegocioRepository extends JpaRepository<ObjetivoNegocio, UUID> {
    List<ObjetivoNegocio> findByRequisitoId(UUID requisitoId);
}
