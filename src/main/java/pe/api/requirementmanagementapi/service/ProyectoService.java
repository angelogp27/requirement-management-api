package pe.api.requirementmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.api.requirementmanagementapi.dto.request.ProyectoRequest;
import pe.api.requirementmanagementapi.dto.response.ProyectoResponse;
import pe.api.requirementmanagementapi.exception.DuplicateResourceException;
import pe.api.requirementmanagementapi.exception.ResourceNotFoundException;
import pe.api.requirementmanagementapi.model.Proyecto;
import pe.api.requirementmanagementapi.model.Usuario;
import pe.api.requirementmanagementapi.model.enums.EstadoProyecto;
import pe.api.requirementmanagementapi.repository.ProyectoRepository;
import pe.api.requirementmanagementapi.repository.UsuarioRepository;

import java.util.UUID;

/**
 * Servicio de negocio para la gestión de proyectos.
 * Implementa CRUD con generación automática de código (RFB-011)
 * y validación de duplicidad de nombres (RFB-012).
 */
@Service
@RequiredArgsConstructor
public class ProyectoService {

    private static final Logger log = LoggerFactory.getLogger(ProyectoService.class);
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo proyecto con código autogenerado (PRJ-001, PRJ-002...).
     * Valida que el nombre no esté duplicado (RFB-012).
     */
    @Transactional
    public ProyectoResponse crearProyecto(ProyectoRequest request) {
        if (proyectoRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("Proyecto", "nombre", request.getNombre());
        }

        Usuario jefe = usuarioRepository.findById(request.getJefeProyectoId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Jefe de Proyecto)", "id", request.getJefeProyectoId()));
        Usuario analista = usuarioRepository.findById(request.getAnalistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Analista)", "id", request.getAnalistaId()));

        String codigo = generarCodigo();

        Proyecto proyecto = Proyecto.builder()
                .codigo(codigo)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .jefeProyecto(jefe)
                .analista(analista)
                .build();

        proyecto = proyectoRepository.save(proyecto);
        log.info("Proyecto creado: {} - {}", proyecto.getCodigo(), proyecto.getNombre());
        return ProyectoResponse.fromEntity(proyecto);
    }

    /**
     * Lista proyectos con paginación (RNFB-003).
     */
    @Transactional(readOnly = true)
    public Page<ProyectoResponse> listarProyectos(Pageable pageable) {
        return proyectoRepository.findAll(pageable)
                .map(ProyectoResponse::fromEntity);
    }

    /**
     * Obtiene un proyecto por su ID con detalles completos.
     */
    @Transactional(readOnly = true)
    public ProyectoResponse obtenerProyecto(UUID id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", id));
        return ProyectoResponse.fromEntity(proyecto);
    }

    /**
     * Actualiza los datos de un proyecto existente.
     */
    @Transactional
    public ProyectoResponse actualizarProyecto(UUID id, ProyectoRequest request) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", id));

        // Validar nombre único si cambió
        if (!proyecto.getNombre().equals(request.getNombre())
                && proyectoRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("Proyecto", "nombre", request.getNombre());
        }

        Usuario jefe = usuarioRepository.findById(request.getJefeProyectoId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Jefe de Proyecto)", "id", request.getJefeProyectoId()));
        Usuario analista = usuarioRepository.findById(request.getAnalistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Analista)", "id", request.getAnalistaId()));

        proyecto.setNombre(request.getNombre());
        proyecto.setDescripcion(request.getDescripcion());
        proyecto.setJefeProyecto(jefe);
        proyecto.setAnalista(analista);

        proyecto = proyectoRepository.save(proyecto);
        log.info("Proyecto actualizado: {}", proyecto.getCodigo());
        return ProyectoResponse.fromEntity(proyecto);
    }

    /**
     * Elimina un proyecto (soft delete marcando como CERRADO).
     * Los requisitos asociados se mantienen por integridad (RFB-010).
     */
    @Transactional
    public void eliminarProyecto(UUID id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", id));
        proyecto.setEstado(EstadoProyecto.CERRADO);
        proyectoRepository.save(proyecto);
        log.info("Proyecto cerrado (soft delete): {}", proyecto.getCodigo());
    }

    /**
     * Genera código único secuencial para proyectos.
     * Formato: PRJ-001, PRJ-002, PRJ-003...
     */
    private String generarCodigo() {
        long count = proyectoRepository.count();
        String codigo;
        do {
            count++;
            codigo = String.format("PRJ-%03d", count);
        } while (proyectoRepository.existsByCodigo(codigo));
        return codigo;
    }

    /**
     * Obtiene la entidad Proyecto directamente (uso interno para ERS, etc.).
     */
    @Transactional(readOnly = true)
    public Proyecto obtenerProyectoEntity(UUID id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", id));
    }

    /**
     * Guarda la entidad Proyecto directamente (uso interno).
     */
    @Transactional
    public Proyecto guardarProyectoEntity(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }
}
