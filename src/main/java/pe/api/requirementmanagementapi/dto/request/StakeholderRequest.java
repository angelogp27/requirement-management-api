package pe.api.requirementmanagementapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear o actualizar un Stakeholder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StakeholderRequest {

    @NotBlank(message = "El nombre del stakeholder es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El rol del stakeholder es obligatorio")
    @Size(max = 50)
    private String rol;

    @Size(max = 100)
    private String organizacion;

    private String nivelInfluencia;

    @Size(max = 200)
    private String contacto;
}
