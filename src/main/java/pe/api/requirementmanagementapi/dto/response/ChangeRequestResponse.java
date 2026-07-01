package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.api.requirementmanagementapi.model.ChangeRequest;
import pe.api.requirementmanagementapi.model.enums.EstadoChangeRequest;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

/**
 * DTO de salida para una Solicitud de Cambio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeRequestResponse {

    private UUID id;
    private UUID requisitoId;
    private String codigoRequisito;
    private UsuarioResponse solicitante;
    private String justificacion;
    private String impactoTecnico;
    private String impactoNegocio;
    private String riesgos;
    private String esfuerzoEstimado;
    private EstadoChangeRequest estado;
    private BigDecimal probabilidadRiesgo;
    private UsuarioResponse revisadoPor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;

    public static ChangeRequestResponse fromEntity(ChangeRequest cr) {
        ChangeRequestResponseBuilder builder = ChangeRequestResponse.builder()
                .id(cr.getId())
                .requisitoId(cr.getRequisito().getId())
                .codigoRequisito(cr.getRequisito().getCodigo())
                .solicitante(UsuarioResponse.fromEntity(cr.getSolicitante()))
                .justificacion(cr.getJustificacion())
                .impactoTecnico(cr.getImpactoTecnico())
                .impactoNegocio(cr.getImpactoNegocio())
                .riesgos(cr.getRiesgos())
                .esfuerzoEstimado(cr.getEsfuerzoEstimado())
                .estado(cr.getEstado())
                .probabilidadRiesgo(cr.getProbabilidadRiesgo())
                .fechaCreacion(cr.getFechaCreacion())
                .fechaResolucion(cr.getFechaResolucion());

        if (cr.getRevisadoPor() != null) {
            builder.revisadoPor(UsuarioResponse.fromEntity(cr.getRevisadoPor()));
        }

        return builder.build();
    }
}
