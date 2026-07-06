package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private long totalProjects;
    private long totalRequirements;
    private long approvedRequirements;
    private long pendingRequirements;
    private List<ProjectStatsDto> projects;
}
