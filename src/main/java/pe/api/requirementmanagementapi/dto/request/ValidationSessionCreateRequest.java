package pe.api.requirementmanagementapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ValidationSessionCreateRequest {
    @NotEmpty(message = "Se requiere al menos un requisito")
    private List<UUID> requisitoIds;
    
    private List<String> correosReviewers;
}
