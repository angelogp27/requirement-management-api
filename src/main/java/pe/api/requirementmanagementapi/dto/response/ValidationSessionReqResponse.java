package pe.api.requirementmanagementapi.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
@Builder
public class ValidationSessionReqResponse {
    private UUID requisitoId;
    private String codigo;
    private String titulo;
    private String descripcion;
    private List<String> criteriosAceptacion;
    private String estadoValidacion;
    private String observaciones;
}
