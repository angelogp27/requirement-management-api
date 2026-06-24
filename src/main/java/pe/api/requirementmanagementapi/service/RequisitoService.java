package pe.api.requirementmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.api.requirementmanagementapi.dto.request.RequisitoRequest;
import pe.api.requirementmanagementapi.dto.response.RequisitoResponse;
import pe.api.requirementmanagementapi.exception.BadRequestException;
import pe.api.requirementmanagementapi.exception.ResourceNotFoundException;
import pe.api.requirementmanagementapi.model.Proyecto;
import pe.api.requirementmanagementapi.model.Requisito;
import pe.api.requirementmanagementapi.model.RequisitoHistorial;
import pe.api.requirementmanagementapi.model.Usuario;
import pe.api.requirementmanagementapi.model.enums.EstadoRequisito;
import pe.api.requirementmanagementapi.model.enums.Prioridad;
import pe.api.requirementmanagementapi.model.enums.Rol;
import pe.api.requirementmanagementapi.model.enums.TipoRequisito;
import pe.api.requirementmanagementapi.repository.ProyectoRepository;
import pe.api.requirementmanagementapi.repository.RequisitoHistorialRepository;
import pe.api.requirementmanagementapi.repository.RequisitoRepository;
import pe.api.requirementmanagementapi.repository.UsuarioRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para la gestión de requisitos.
 * Implementa CRUD con generación de código (RFB-019),
 * registro automático de historial (RFB-021, RFB-027),
 * asignación de técnicos (RFB-022) y gestión de dependencias.
 */
@Service
@RequiredArgsConstructor
public class RequisitoService {

    private static final Logger log = LoggerFactory.getLogger(RequisitoService.class);
    private final RequisitoRepository requisitoRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RequisitoHistorialRepository historialRepository;

    /**
     * Crea un nuevo requisito con código autogenerado (RF-001, RNF-001...).
     * Registra la creación en el historial de cambios (RFB-027).
     */
    @Transactional
    public RequisitoResponse crearRequisito(UUID proyectoId, RequisitoRequest request, UUID usuarioId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto", "id", proyectoId));

        Usuario solicitante = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Solicitante)", "id", request.getSolicitanteId()));

        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Autor)", "id", usuarioId));

        String codigo = generarCodigo(proyectoId, request.getTipo());

        Requisito requisito = Requisito.builder()
                .proyecto(proyecto)
                .codigo(codigo)
                .tipo(request.getTipo())
                .descripcion(request.getDescripcion())
                .solicitante(solicitante)
                .prioridad(request.getPrioridad())
                .costoEstimado(request.getCostoEstimado())
                .build();

        // Asignar técnico si se proporcionó
        if (request.getAsignadoAId() != null) {
            Usuario asignado = usuarioRepository.findById(request.getAsignadoAId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario (Asignado)", "id", request.getAsignadoAId()));
            requisito.setAsignadoA(asignado);
        }

        requisito = requisitoRepository.save(requisito);

        // Registrar en historial (INSERT)
        registrarCambio(requisito, autor, "INSERT", null, null,
                requisito.getCodigo(), "Requisito creado: " + requisito.getCodigo());

        log.info("Requisito creado: {} en proyecto {}", requisito.getCodigo(), proyecto.getCodigo());
        return RequisitoResponse.fromEntity(requisito);
    }

    /**
     * Lista requisitos de un proyecto con filtros opcionales y paginación.
     * Soporta RFB-014 y RFB-015.
     */
    @Transactional(readOnly = true)
    public Page<RequisitoResponse> listarRequisitos(UUID proyectoId, String estado,
                                                     String prioridad, String tipo, Pageable pageable) {
        // Validar que el proyecto existe
        if (!proyectoRepository.existsById(proyectoId)) {
            throw new ResourceNotFoundException("Proyecto", "id", proyectoId);
        }

        EstadoRequisito estadoEnum = estado != null ? EstadoRequisito.valueOf(estado) : null;
        Prioridad prioridadEnum = prioridad != null ? Prioridad.valueOf(prioridad) : null;
        TipoRequisito tipoEnum = tipo != null ? TipoRequisito.valueOf(tipo) : null;

        return requisitoRepository.findByFilters(proyectoId, estadoEnum, prioridadEnum, tipoEnum, pageable)
                .map(RequisitoResponse::fromEntity);
    }

    /**
     * Obtiene un requisito específico por proyecto e ID.
     */
    @Transactional(readOnly = true)
    public RequisitoResponse obtenerRequisito(UUID proyectoId, UUID requisitoId) {
        Requisito requisito = requisitoRepository.findByIdAndProyectoId(requisitoId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito", "id", requisitoId));
        return RequisitoResponse.fromEntity(requisito);
    }

    /**
     * Actualiza un requisito y registra automáticamente cada cambio en el historial.
     * Soporta RFB-017 y RFB-021 (historial de versiones).
     */
    @Transactional
    public RequisitoResponse actualizarRequisito(UUID proyectoId, UUID requisitoId,
                                                  RequisitoRequest request, UUID usuarioId) {
        Requisito requisito = requisitoRepository.findByIdAndProyectoId(requisitoId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito", "id", requisitoId));

        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Autor)", "id", usuarioId));

        // Detectar y registrar cambios campo por campo
        if (!Objects.equals(requisito.getDescripcion(), request.getDescripcion())) {
            registrarCambio(requisito, autor, "UPDATE", "descripcion",
                    requisito.getDescripcion(), request.getDescripcion(), null);
            requisito.setDescripcion(request.getDescripcion());
        }

        if (!Objects.equals(requisito.getPrioridad(), request.getPrioridad())) {
            registrarCambio(requisito, autor, "UPDATE", "prioridad",
                    requisito.getPrioridad().name(), request.getPrioridad().name(), null);
            requisito.setPrioridad(request.getPrioridad());
        }

        if (!Objects.equals(requisito.getCostoEstimado(), request.getCostoEstimado())) {
            registrarCambio(requisito, autor, "UPDATE", "costo_estimado",
                    requisito.getCostoEstimado() != null ? requisito.getCostoEstimado().toString() : null,
                    request.getCostoEstimado() != null ? request.getCostoEstimado().toString() : null, null);
            requisito.setCostoEstimado(request.getCostoEstimado());
        }

        if (!Objects.equals(requisito.getTipo(), request.getTipo())) {
            registrarCambio(requisito, autor, "UPDATE", "tipo",
                    requisito.getTipo().name(), request.getTipo().name(), null);
            requisito.setTipo(request.getTipo());
        }

        // Actualizar solicitante si cambió
        if (!Objects.equals(requisito.getSolicitante().getId(), request.getSolicitanteId())) {
            Usuario nuevoSolicitante = usuarioRepository.findById(request.getSolicitanteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario (Solicitante)", "id", request.getSolicitanteId()));
            registrarCambio(requisito, autor, "UPDATE", "solicitante_id",
                    requisito.getSolicitante().getId().toString(), request.getSolicitanteId().toString(), null);
            requisito.setSolicitante(nuevoSolicitante);
        }

        // Actualizar asignado si cambió
        UUID asignadoActualId = requisito.getAsignadoA() != null ? requisito.getAsignadoA().getId() : null;
        if (!Objects.equals(asignadoActualId, request.getAsignadoAId())) {
            if (request.getAsignadoAId() != null) {
                Usuario nuevoAsignado = usuarioRepository.findById(request.getAsignadoAId())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario (Asignado)", "id", request.getAsignadoAId()));
                requisito.setAsignadoA(nuevoAsignado);
            } else {
                requisito.setAsignadoA(null);
            }
            registrarCambio(requisito, autor, "UPDATE", "asignado_a_id",
                    asignadoActualId != null ? asignadoActualId.toString() : null,
                    request.getAsignadoAId() != null ? request.getAsignadoAId().toString() : null, null);
        }

        requisito = requisitoRepository.save(requisito);
        log.info("Requisito actualizado: {}", requisito.getCodigo());
        return RequisitoResponse.fromEntity(requisito);
    }

    /**
     * Elimina un requisito y registra la acción en el historial.
     */
    @Transactional
    public void eliminarRequisito(UUID proyectoId, UUID requisitoId, UUID usuarioId) {
        Requisito requisito = requisitoRepository.findByIdAndProyectoId(requisitoId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito", "id", requisitoId));

        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Autor)", "id", usuarioId));

        registrarCambio(requisito, autor, "DELETE", null,
                requisito.getCodigo(), null, "Requisito eliminado: " + requisito.getCodigo());

        requisitoRepository.delete(requisito);
        log.info("Requisito eliminado: {}", requisito.getCodigo());
    }

    /**
     * Cambia el estado de un requisito siguiendo el flujo definido.
     * Flujo: REGISTRADO → EN_ANALISIS → VALIDADO → APROBADO
     */
    @Transactional
    public RequisitoResponse cambiarEstado(UUID proyectoId, UUID requisitoId,
                                            EstadoRequisito nuevoEstado, UUID usuarioId) {
        Requisito requisito = requisitoRepository.findByIdAndProyectoId(requisitoId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito", "id", requisitoId));

        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Autor)", "id", usuarioId));

        String estadoAnterior = requisito.getEstado().name();
        registrarCambio(requisito, autor, "UPDATE", "estado",
                estadoAnterior, nuevoEstado.name(), "Cambio de estado");

        requisito.setEstado(nuevoEstado);
        requisito = requisitoRepository.save(requisito);
        log.info("Requisito {} cambió de estado: {} → {}", requisito.getCodigo(), estadoAnterior, nuevoEstado);
        return RequisitoResponse.fromEntity(requisito);
    }

    /**
     * Asigna un requisito a un usuario técnico (RFB-022).
     * Valida que el usuario tenga rol TECNICO (RFB-026).
     */
    @Transactional
    public RequisitoResponse asignarUsuario(UUID proyectoId, UUID requisitoId,
                                             UUID asignadoId, UUID usuarioId) {
        Requisito requisito = requisitoRepository.findByIdAndProyectoId(requisitoId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito", "id", requisitoId));

        Usuario asignado = usuarioRepository.findById(asignadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Asignado)", "id", asignadoId));

        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (Autor)", "id", usuarioId));

        // Validar rol técnico (RFB-026)
        if (asignado.getRol() != Rol.TECNICO) {
            throw new BadRequestException("Solo usuarios con rol TECNICO pueden ser asignados a requisitos");
        }

        String asignadoAnterior = requisito.getAsignadoA() != null
                ? requisito.getAsignadoA().getUsername() : "Sin asignar";

        registrarCambio(requisito, autor, "UPDATE", "asignado_a_id",
                asignadoAnterior, asignado.getUsername(),
                "Asignación de requisito a " + asignado.getUsername());

        requisito.setAsignadoA(asignado);
        requisito = requisitoRepository.save(requisito);
        log.info("Requisito {} asignado a {}", requisito.getCodigo(), asignado.getUsername());
        return RequisitoResponse.fromEntity(requisito);
    }

    /**
     * Agrega una dependencia/precedencia entre requisitos del mismo proyecto.
     */
    @Transactional
    public RequisitoResponse agregarDependencia(UUID proyectoId, UUID requisitoId, UUID dependenciaId) {
        Requisito requisito = requisitoRepository.findByIdAndProyectoId(requisitoId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito", "id", requisitoId));

        Requisito dependencia = requisitoRepository.findByIdAndProyectoId(dependenciaId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito (Dependencia)", "id", dependenciaId));

        if (requisitoId.equals(dependenciaId)) {
            throw new BadRequestException("Un requisito no puede depender de sí mismo");
        }

        requisito.getDependencias().add(dependencia);
        requisito = requisitoRepository.save(requisito);
        log.info("Dependencia agregada: {} depende de {}", requisito.getCodigo(), dependencia.getCodigo());
        return RequisitoResponse.fromEntity(requisito);
    }

    /**
     * Obtiene las dependencias de un requisito.
     */
    @Transactional(readOnly = true)
    public List<RequisitoResponse> obtenerDependencias(UUID proyectoId, UUID requisitoId) {
        Requisito requisito = requisitoRepository.findByIdAndProyectoId(requisitoId, proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Requisito", "id", requisitoId));

        return requisito.getDependencias().stream()
                .map(RequisitoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la matriz de trazabilidad de un proyecto.
     * Retorna todos los requisitos con sus asignaciones (RFB-023).
     */
    @Transactional(readOnly = true)
    public List<RequisitoResponse> obtenerTrazabilidad(UUID proyectoId) {
        if (!proyectoRepository.existsById(proyectoId)) {
            throw new ResourceNotFoundException("Proyecto", "id", proyectoId);
        }

        return requisitoRepository.findByProyectoId(proyectoId).stream()
                .map(RequisitoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Genera código único secuencial por tipo dentro del proyecto.
     * Formato: RF-001, RF-002, RNF-001... (RFB-019).
     */
    private String generarCodigo(UUID proyectoId, TipoRequisito tipo) {
        long count = requisitoRepository.countByProyectoIdAndTipo(proyectoId, tipo);
        String codigo;
        do {
            count++;
            codigo = String.format("%s-%03d", tipo.name(), count);
        } while (requisitoRepository.existsByProyectoIdAndCodigo(proyectoId, codigo));
        return codigo;
    }

    /**
     * Registra un cambio en el historial de auditoría (insert-only).
     * Soporta RFB-027 a RFB-029.
     */
    private void registrarCambio(Requisito requisito, Usuario autor, String tipoCambio,
                                  String campo, String valorAnterior, String valorNuevo,
                                  String descripcion) {
        RequisitoHistorial historial = RequisitoHistorial.builder()
                .requisito(requisito)
                .usuario(autor)
                .tipoCambio(tipoCambio)
                .campoModificado(campo)
                .valorAnterior(valorAnterior)
                .valorNuevo(valorNuevo)
                .descripcionCambio(descripcion)
                .build();
        historialRepository.save(historial);
    }
}
