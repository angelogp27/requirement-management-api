package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.RequisitoVersion;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequisitoVersionRepository extends JpaRepository<RequisitoVersion, UUID> {
    List<RequisitoVersion> findByRequisitoIdOrderByFechaCreacionDesc(UUID requisitoId);
    long countByRequisitoId(UUID requisitoId);
}
