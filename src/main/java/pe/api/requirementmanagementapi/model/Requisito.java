package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;
import pe.api.requirementmanagementapi.model.enums.EstadoRequisito;
import pe.api.requirementmanagementapi.model.enums.Prioridad;
import pe.api.requirementmanagementapi.model.enums.TipoRequisito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entidad que representa un requisito funcional o no funcional.
 * Mapeada a la tabla 'requisitos' de PostgreSQL.
 * El código es único dentro de cada proyecto (RFB-019).
 * Soporta: RFB-013 a RFB-021 (Gestión de Requisitos).
 */
@Entity
@Table(name = "requisitos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_requisito_codigo_proyecto",
                columnNames = {"proyecto_id", "codigo"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requisito {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private TipoRequisito tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "necesidad_cubierta", columnDefinition = "TEXT")
    private String necesidadCubierta;

    @Column(name = "iteracion_sprint", length = 50)
    private String iteracionSprint;

    @Column(name = "criterios_aceptacion", columnDefinition = "TEXT")
    private String criteriosAceptacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoRequisito estado = EstadoRequisito.REGISTRADO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Prioridad prioridad = Prioridad.MEDIA;

    @Column(name = "costo_estimado", precision = 10, scale = 2)
    private BigDecimal costoEstimado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a_id")
    private Usuario asignadoA;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "nivel_ceremonia", length = 10)
    private String nivelCeremonia;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalles_caso_uso", columnDefinition = "jsonb")
    private Map<String, Object> detallesCasoUso;

    /**
     * Relación de dependencias/precedencias entre requisitos.
     * Un requisito puede depender de varios requisitos precedentes.
     * Soporta RF-002 (Precedencia) y RFB-022 a RFB-026 (Trazabilidad).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "requisitos_dependencias",
            joinColumns = @JoinColumn(name = "requisito_id"),
            inverseJoinColumns = @JoinColumn(name = "requisito_precedente_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Requisito> dependencias = new HashSet<>();

    /**
     * Relación con Stakeholders.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "requisitos_stakeholders",
            joinColumns = @JoinColumn(name = "requisito_id"),
            inverseJoinColumns = @JoinColumn(name = "stakeholder_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Stakeholder> stakeholders = new HashSet<>();

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
