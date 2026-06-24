package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;
import pe.api.requirementmanagementapi.model.enums.EstadoProyecto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un proyecto del sistema REM.
 * Mapeada a la tabla 'proyectos' de PostgreSQL.
 * Soporta: RFB-006 a RFB-012 (Gestión de Proyectos).
 */
@Entity
@Table(name = "proyectos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jefe_proyecto_id", nullable = false)
    private Usuario jefeProyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analista_id", nullable = false)
    private Usuario analista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoProyecto estado = EstadoProyecto.ACTIVO;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
