package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.api.requirementmanagementapi.model.Usuario;
import pe.api.requirementmanagementapi.model.enums.Rol;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para la entidad Usuario.
 * Excluye el campo password por seguridad (RNFB-011).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private UUID id;
    private String username;
    private String email;
    private Rol rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaConexion;

    /**
     * Convierte una entidad Usuario a su DTO de respuesta.
     */
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .fechaUltimaConexion(usuario.getFechaUltimaConexion())
                .build();
    }
}
