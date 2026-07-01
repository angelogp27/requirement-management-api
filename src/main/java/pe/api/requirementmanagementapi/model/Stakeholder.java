package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un stakeholder (parte interesada).
 * Mapeada a la tabla 'stakeholders' de PostgreSQL.
 * Soporta: Módulo de Captura - Gestión de Stakeholders.
 */
@Entity
@Table(name = "stakeholders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stakeholder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String rol;

    @Column(length = 100)
    private String organizacion;

    @Column(name = "nivel_influencia", length = 20)
    @Builder.Default
    private String nivelInfluencia = "MEDIO";

    @Column(length = 200)
    private String contacto;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
