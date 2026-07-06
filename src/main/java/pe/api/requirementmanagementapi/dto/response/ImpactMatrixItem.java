package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactMatrixItem {
    private String elementoId; // e.g. REQ-012, TC-08, OBJ-02
    private String titulo;
    private String tipoElemento; // e.g. Requisito de Sistema, Caso de Prueba, Objetivo de Negocio
    private String tipoRelacion; // e.g. Dependiente (Hijo), Cobertura, Trazabilidad
    private String nivelImpacto; // ALTO, MEDIO, BAJO, CRITICO
    private String esfuerzoEstimado; // e.g. 12 hrs.
}
