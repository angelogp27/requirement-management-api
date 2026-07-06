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
public class ImpactMatrixResponse {
    private Double riesgoProbabilidad; // From Bayesian Network
    private String nivelRiesgoGeneral; // ALTO, MEDIO, BAJO
    private List<ImpactMatrixItem> items;
}
