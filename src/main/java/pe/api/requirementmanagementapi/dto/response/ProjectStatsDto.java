package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectStatsDto {
    private UUID id;
    private String codigo;
    private String nombre;
    private long totalRequirements;
    private long approvedRequirements;
    private long pendingRequirements;
}
