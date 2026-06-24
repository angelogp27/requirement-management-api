package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.api.requirementmanagementapi.model.Requisito;
import pe.api.requirementmanagementapi.model.enums.EstadoRequisito;
import pe.api.requirementmanagementapi.model.enums.Prioridad;
import pe.api.requirementmanagementapi.model.enums.TipoRequisito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para la entidad Requisito.
 * Incluye información resumida de solicitante y usuario asignado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitoResponse {

    private UUID id;
    private String codigo;
    private TipoRequisito tipo;
    private String descripcion;
    private UsuarioResponse solicitante;
    private EstadoRequisito estado;
    private Prioridad prioridad;
    private BigDecimal costoEstimado;
    private UsuarioResponse asignadoA;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    /**
     * Convierte una entidad Requisito a su DTO de respuesta.
     */
    public static RequisitoResponse fromEntity(Requisito requisito) {
        RequisitoResponseBuilder builder = RequisitoResponse.builder()
                .id(requisito.getId())
                .codigo(requisito.getCodigo())
                .tipo(requisito.getTipo())
                .descripcion(requisito.getDescripcion())
                .solicitante(UsuarioResponse.fromEntity(requisito.getSolicitante()))
                .estado(requisito.getEstado())
                .prioridad(requisito.getPrioridad())
                .costoEstimado(requisito.getCostoEstimado())
                .fechaCreacion(requisito.getFechaCreacion())
                .fechaActualizacion(requisito.getFechaActualizacion());

        if (requisito.getAsignadoA() != null) {
            builder.asignadoA(UsuarioResponse.fromEntity(requisito.getAsignadoA()));
        }

        return builder.build();
    }
}
