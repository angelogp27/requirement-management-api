package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.ValidationSessionReq;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ValidationSessionReqRepository extends JpaRepository<ValidationSessionReq, UUID> {
    Optional<ValidationSessionReq> findBySesionIdAndRequisitoId(UUID sesionId, UUID requisitoId);
}
