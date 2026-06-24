package pe.api.requirementmanagementapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.Proyecto;
import pe.api.requirementmanagementapi.model.enums.EstadoProyecto;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Proyecto.
 * Proporciona acceso paginado y filtrado a la tabla 'proyectos'.
 * Soporta RNFB-003: Paginación por defecto.
 */
@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {

    Optional<Proyecto> findByCodigo(String codigo);

    Page<Proyecto> findByEstado(EstadoProyecto estado, Pageable pageable);

    Page<Proyecto> findByJefeProyectoId(UUID jefeProyectoId, Pageable pageable);

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    long count();
}
