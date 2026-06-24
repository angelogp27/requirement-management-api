package pe.api.requirementmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.api.requirementmanagementapi.dto.response.HistorialResponse;
import pe.api.requirementmanagementapi.exception.ResourceNotFoundException;
import pe.api.requirementmanagementapi.repository.RequisitoHistorialRepository;
import pe.api.requirementmanagementapi.repository.RequisitoRepository;

import java.util.UUID;

/**
 * Servicio de negocio para consulta del historial de cambios.
 * Los registros de auditoría son inmutables (insert-only, RNFB-029).
 * La creación de registros se realiza en RequisitoService.
 */
@Service
@RequiredArgsConstructor
public class RequisitoHistorialService {

    private static final Logger log = LoggerFactory.getLogger(RequisitoHistorialService.class);
    private final RequisitoHistorialRepository historialRepository;
    private final RequisitoRepository requisitoRepository;

    /**
     * Obtiene el historial de cambios de un requisito con paginación.
     * Ordenado cronológicamente (más recientes primero). Soporta RFB-028.
     */
    @Transactional(readOnly = true)
    public Page<HistorialResponse> obtenerHistorial(UUID proyectoId, UUID requisitoId, Pageable pageable) {
        // Validar que el requisito existe y pertenece al proyecto
        requisitoRepository.findByIdAndProyectoId(requisitoId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito", "id", requisitoId));

        return historialRepository.findByRequisitoIdOrderByFechaCambioDesc(requisitoId, pageable)
                .map(HistorialResponse::fromEntity);
    }
}
