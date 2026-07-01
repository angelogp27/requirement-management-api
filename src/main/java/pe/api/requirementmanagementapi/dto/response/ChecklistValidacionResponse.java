package pe.api.requirementmanagementapi.dto.response;

import lombok.Builder;
import lombok.Data;
import pe.api.requirementmanagementapi.model.ChecklistValidacion;

import java.util.UUID;

@Data
@Builder
public class ChecklistValidacionResponse {
    private UUID id;
    private UUID requisitoId;
    private UsuarioResponse evaluador;
    private boolean esAtomico;
    private boolean esTestable;
    private boolean esFactible;
    private boolean noAmbiguo;
    private boolean esCompleto;
    private String observaciones;

    public static ChecklistValidacionResponse fromEntity(ChecklistValidacion entity) {
        return ChecklistValidacionResponse.builder()
                .id(entity.getId())
                .requisitoId(entity.getRequisito().getId())
                .evaluador(UsuarioResponse.fromEntity(entity.getEvaluador()))
                .esAtomico(entity.getEsAtomico() != null && entity.getEsAtomico())
                .esTestable(entity.getEsTestable() != null && entity.getEsTestable())
                .esFactible(entity.getEsFactible() != null && entity.getEsFactible())
                .noAmbiguo(entity.getNoAmbiguo() != null && entity.getNoAmbiguo())
                .esCompleto(entity.getEsCompleto() != null && entity.getEsCompleto())
                .observaciones(entity.getObservaciones())
                .build();
    }
}
