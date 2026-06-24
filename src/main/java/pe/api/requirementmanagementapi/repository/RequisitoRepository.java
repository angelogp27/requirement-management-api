package pe.api.requirementmanagementapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.Requisito;
import pe.api.requirementmanagementapi.model.enums.EstadoRequisito;
import pe.api.requirementmanagementapi.model.enums.Prioridad;
import pe.api.requirementmanagementapi.model.enums.TipoRequisito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Requisito.
 * Proporciona acceso paginado, filtrado y conteo por proyecto.
 * Soporta RFB-014, RFB-015 (listar y filtrar requisitos).
 */
@Repository
public interface RequisitoRepository extends JpaRepository<Requisito, UUID> {

    Page<Requisito> findByProyectoId(UUID proyectoId, Pageable pageable);

    List<Requisito> findByProyectoId(UUID proyectoId);

    Optional<Requisito> findByIdAndProyectoId(UUID id, UUID proyectoId);

    /**
     * Filtrado dinámico de requisitos por proyecto con parámetros opcionales.
     * Los parámetros nulos se ignoran en la consulta (RFB-015).
     */
    @Query("SELECT r FROM Requisito r WHERE r.proyecto.id = :proyectoId " +
            "AND (:estado IS NULL OR r.estado = :estado) " +
            "AND (:prioridad IS NULL OR r.prioridad = :prioridad) " +
            "AND (:tipo IS NULL OR r.tipo = :tipo)")
    Page<Requisito> findByFilters(
            @Param("proyectoId") UUID proyectoId,
            @Param("estado") EstadoRequisito estado,
            @Param("prioridad") Prioridad prioridad,
            @Param("tipo") TipoRequisito tipo,
            Pageable pageable);

    List<Requisito> findByAsignadoAId(UUID usuarioId);

    long countByProyectoIdAndTipo(UUID proyectoId, TipoRequisito tipo);

    long countByProyectoId(UUID proyectoId);

    boolean existsByCodigo(String codigo);

    boolean existsByProyectoIdAndCodigo(UUID proyectoId, String codigo);
}
