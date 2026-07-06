package pe.api.requirementmanagementapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO de entrada para crear una Solicitud de Cambio (Change Request).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeRequestRequest {

    @NotNull(message = "El ID del requisito es obligatorio")
    private UUID requisitoId;

    @NotNull(message = "El ID del solicitante es obligatorio")
    private UUID solicitanteId;

    @NotBlank(message = "La justificación es obligatoria")
    private String justificacion;

    @NotBlank(message = "El texto propuesto es obligatorio")
    private String textoPropuesto;

    private String impactoTecnico;
    private String impactoNegocio;
    private String riesgos;
    private String esfuerzoEstimado;
}
