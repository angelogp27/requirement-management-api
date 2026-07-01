package pe.api.requirementmanagementapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChecklistValidacionRequest {
    @NotNull(message = "El evaluador es obligatorio")
    private UUID evaluadorId;
    
    private boolean esAtomico;
    private boolean esTestable;
    private boolean esFactible;
    private boolean noAmbiguo;
    private boolean esCompleto;
    private String observaciones;
}
