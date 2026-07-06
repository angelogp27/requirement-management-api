package pe.api.requirementmanagementapi.dto.response;

import lombok.Builder;
import lombok.Data;
import pe.api.requirementmanagementapi.model.RequisitoVersion;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RequisitoVersionResponse {
    private UUID id;
    private UUID requisitoId;
    private String version;
    private String snapshotJson;
    private String changeRequestJustificacion;
    private String changeRequestEstado;
    private String creadoPorNombre;
    private LocalDateTime fechaCreacion;

    public static RequisitoVersionResponse fromEntity(RequisitoVersion entity) {
        String justificacion = "";
        String estado = "";
        if (entity.getChangeRequest() != null) {
            justificacion = entity.getChangeRequest().getJustificacion();
            estado = entity.getChangeRequest().getEstado().name();
        } else {
            justificacion = "Línea Base Inicial";
            estado = "Línea Base";
        }
        
        return RequisitoVersionResponse.builder()
                .id(entity.getId())
                .requisitoId(entity.getRequisito().getId())
                .version(entity.getVersion())
                .snapshotJson(entity.getSnapshotJson())
                .changeRequestJustificacion(justificacion)
                .changeRequestEstado(estado)
                .creadoPorNombre(entity.getCreadoPor().getUsername())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
