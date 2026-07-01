package pe.api.requirementmanagementapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.ComentarioReview;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComentarioReviewRepository extends JpaRepository<ComentarioReview, UUID> {
    List<ComentarioReview> findByRequisitoIdOrderByFechaCreacionAsc(UUID requisitoId);
}
