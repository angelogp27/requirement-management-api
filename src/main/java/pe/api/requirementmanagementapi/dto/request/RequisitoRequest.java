package pe.api.requirementmanagementapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.api.requirementmanagementapi.model.enums.Prioridad;
import pe.api.requirementmanagementapi.model.enums.TipoRequisito;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * DTO de entrada para crear o actualizar un requisito.
 * Incluye validaciones de campos obligatorios (RFB-020, RNFB-015).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitoRequest {

    @NotNull(message = "El tipo de requisito es obligatorio (RF o RNF)")
    private TipoRequisito tipo;

    @NotBlank(message = "La descripción del requisito es obligatoria")
    @Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
    private String descripcion;

    /** Necesidad de negocio que este requisito cubre */
    private String necesidadCubierta;

    /** Iteración o Sprint en el que se planifica */
    private String iteracionSprint;

    /** Criterios de aceptación en formato texto/Markdown */
    private String criteriosAceptacion;

    @NotNull(message = "El ID del solicitante es obligatorio")
    private UUID solicitanteId;

    @NotNull(message = "La prioridad es obligatoria")
    private Prioridad prioridad;

    private UUID asignadoAId;

    private String nivelCeremonia;
    private Map<String, Object> detallesCasoUso;
}
