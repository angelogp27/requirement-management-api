package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.api.requirementmanagementapi.model.RequisitoHistorial;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para el historial de cambios de un requisito.
 * Registros inmutables de auditoría (RNFB-029).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialResponse {

    private UUID id;
    private UUID requisitoId;
    private UsuarioResponse usuario;
    private String tipoCambio;
    private String campoModificado;
    private String valorAnterior;
    private String valorNuevo;
    private String descripcionCambio;
    private LocalDateTime fechaCambio;

    /**
     * Convierte una entidad RequisitoHistorial a su DTO de respuesta.
     */
    public static HistorialResponse fromEntity(RequisitoHistorial historial) {
        return HistorialResponse.builder()
                .id(historial.getId())
                .requisitoId(historial.getRequisito().getId())
                .usuario(UsuarioResponse.fromEntity(historial.getUsuario()))
                .tipoCambio(historial.getTipoCambio())
                .campoModificado(historial.getCampoModificado())
                .valorAnterior(historial.getValorAnterior())
                .valorNuevo(historial.getValorNuevo())
                .descripcionCambio(historial.getDescripcionCambio())
                .fechaCambio(historial.getFechaCambio())
                .build();
    }
}
