package pe.api.requirementmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.api.requirementmanagementapi.model.Proyecto;
import pe.api.requirementmanagementapi.model.enums.EstadoProyecto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para la entidad Proyecto.
 * Incluye información resumida de jefe de proyecto y analista.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoResponse {

    private UUID id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private UsuarioResponse jefeProyecto;
    private UsuarioResponse analista;
    private EstadoProyecto estado;
    private String ersMarkdown;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    /**
     * Convierte una entidad Proyecto a su DTO de respuesta.
     */
    public static ProyectoResponse fromEntity(Proyecto proyecto) {
        return ProyectoResponse.builder()
                .id(proyecto.getId())
                .codigo(proyecto.getCodigo())
                .nombre(proyecto.getNombre())
                .descripcion(proyecto.getDescripcion())
                .jefeProyecto(UsuarioResponse.fromEntity(proyecto.getJefeProyecto()))
                .analista(UsuarioResponse.fromEntity(proyecto.getAnalista()))
                .estado(proyecto.getEstado())
                .ersMarkdown(proyecto.getErsMarkdown())
                .fechaCreacion(proyecto.getFechaCreacion())
                .fechaActualizacion(proyecto.getFechaActualizacion())
                .build();
    }
}
