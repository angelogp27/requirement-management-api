package pe.api.requirementmanagementapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear un comentario de revisión.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComentarioReviewRequest {

    @NotNull(message = "El usuario es obligatorio")
    private java.util.UUID usuarioId;

    @NotBlank(message = "El contenido del comentario es obligatorio")
    private String contenido;
}
