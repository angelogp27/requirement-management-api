package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.api.requirementmanagementapi.model.Stakeholder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para la entidad Stakeholder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StakeholderResponse {

    private UUID id;
    private String nombre;
    private String rol;
    private String organizacion;
    private String nivelInfluencia;
    private String contacto;
    private LocalDateTime fechaCreacion;

    public static StakeholderResponse fromEntity(Stakeholder s) {
        return StakeholderResponse.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .rol(s.getRol())
                .organizacion(s.getOrganizacion())
                .nivelInfluencia(s.getNivelInfluencia())
                .contacto(s.getContacto())
                .fechaCreacion(s.getFechaCreacion())
                .build();
    }
}
