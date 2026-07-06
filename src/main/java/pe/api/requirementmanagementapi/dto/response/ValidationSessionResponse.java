package pe.api.requirementmanagementapi.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ValidationSessionResponse {
    private UUID id;
    private UUID proyectoId;
    private String titulo;
    private String estado;
    private LocalDateTime fechaCreacion;
    private List<String> reviewers;
    private List<ValidationSessionReqResponse> requisitos;
}
