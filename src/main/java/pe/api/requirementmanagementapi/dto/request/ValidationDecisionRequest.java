package pe.api.requirementmanagementapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidationDecisionRequest {
    @NotNull
    private String decision; // "APPROVED" or "REJECTED"
    
    private String observaciones;
}
