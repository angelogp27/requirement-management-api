package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.ValidationSession;

import java.util.List;
import java.util.UUID;

@Repository
public interface ValidationSessionRepository extends JpaRepository<ValidationSession, UUID> {
    List<ValidationSession> findByProyectoId(UUID proyectoId);
}
