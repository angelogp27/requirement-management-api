package pe.api.requirementmanagementapi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.api.requirementmanagementapi.dto.request.UsuarioRequest;
import pe.api.requirementmanagementapi.dto.response.UsuarioResponse;
import pe.api.requirementmanagementapi.exception.DuplicateResourceException;
import pe.api.requirementmanagementapi.exception.ResourceNotFoundException;
import pe.api.requirementmanagementapi.model.Usuario;
import pe.api.requirementmanagementapi.repository.UsuarioRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para la gestión de usuarios.
 * Implementa CRUD completo con soft delete (activo = false).
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo usuario validando unicidad de username y email.
     */
    @Transactional
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Usuario", "username", request.getUsername());
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Usuario", "email", request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(request.getPassword()) // TODO: Encriptar con BCrypt en fase JWT
                .email(request.getEmail())
                .rol(request.getRol())
                .build();

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario creado: {} (rol: {})", usuario.getUsername(), usuario.getRol());
        return UsuarioResponse.fromEntity(usuario);
    }

    /**
     * Lista todos los usuarios activos del sistema.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su ID.
     */
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return UsuarioResponse.fromEntity(usuario);
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    @Transactional
    public UsuarioResponse actualizarUsuario(UUID id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Validar unicidad si cambiaron username o email
        if (!usuario.getUsername().equals(request.getUsername())
                && usuarioRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Usuario", "username", request.getUsername());
        }
        if (!usuario.getEmail().equals(request.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Usuario", "email", request.getEmail());
        }

        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword()); // TODO: Encriptar con BCrypt
        usuario.setEmail(request.getEmail());
        usuario.setRol(request.getRol());

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario actualizado: {}", usuario.getUsername());
        return UsuarioResponse.fromEntity(usuario);
    }

    /**
     * Desactiva un usuario (soft delete).
     * No se elimina físicamente de la BD para mantener integridad referencial.
     */
    @Transactional
    public void eliminarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado: {}", usuario.getUsername());
    }
}
