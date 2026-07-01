package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un checklist de validación de calidad.
 * Mapeada a la tabla 'checklist_validacion' de PostgreSQL.
 * Criterios: Atomicidad, Testabilidad, Factibilidad, No ambigüedad, Completitud.
 * Soporta: Módulo de Validación - Inspección estática.
 */
@Entity
@Table(name = "checklist_validacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistValidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisito_id", nullable = false)
    private Requisito requisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluador_id", nullable = false)
    private Usuario evaluador;

    @Column(name = "es_atomico")
    @Builder.Default
    private Boolean esAtomico = false;

    @Column(name = "es_testable")
    @Builder.Default
    private Boolean esTestable = false;

    @Column(name = "es_factible")
    @Builder.Default
    private Boolean esFactible = false;

    @Column(name = "no_ambiguo")
    @Builder.Default
    private Boolean noAmbiguo = false;

    @Column(name = "es_completo")
    @Builder.Default
    private Boolean esCompleto = false;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_evaluacion", updatable = false)
    private LocalDateTime fechaEvaluacion;

    @PrePersist
    protected void onCreate() {
        this.fechaEvaluacion = LocalDateTime.now();
    }
}
