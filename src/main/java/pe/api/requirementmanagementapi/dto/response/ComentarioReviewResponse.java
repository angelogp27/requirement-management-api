package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.api.requirementmanagementapi.model.ComentarioReview;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para un comentario de revisión.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComentarioReviewResponse {

    private UUID id;
    private UsuarioResponse usuario;
    private String contenido;
    private LocalDateTime fechaCreacion;

    public static ComentarioReviewResponse fromEntity(ComentarioReview c) {
        return ComentarioReviewResponse.builder()
                .id(c.getId())
                .usuario(UsuarioResponse.fromEntity(c.getUsuario()))
                .contenido(c.getContenido())
                .fechaCreacion(c.getFechaCreacion())
                .build();
    }
}
