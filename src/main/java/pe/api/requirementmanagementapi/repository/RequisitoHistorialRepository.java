package pe.api.requirementmanagementapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.api.requirementmanagementapi.model.RequisitoHistorial;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad RequisitoHistorial.
 * Proporciona acceso al historial de cambios de requisitos.
 * Los registros son insert-only (RNFB-029: auditoría inmutable).
 * Soporta: RFB-028 (Obtener Historial).
 */
@Repository
public interface RequisitoHistorialRepository extends JpaRepository<RequisitoHistorial, UUID> {

    Page<RequisitoHistorial> findByRequisitoIdOrderByFechaCambioDesc(UUID requisitoId, Pageable pageable);

    List<RequisitoHistorial> findByRequisitoIdOrderByFechaCambioDesc(UUID requisitoId);

    List<RequisitoHistorial> findByUsuarioId(UUID usuarioId);
}
