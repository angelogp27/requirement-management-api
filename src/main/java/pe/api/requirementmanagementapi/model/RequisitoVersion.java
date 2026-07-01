package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa una versión inmutable de un requisito.
 * Mapeada a la tabla 'requisitos_versiones' de PostgreSQL.
 * Almacena un snapshot JSON del requisito antes de un cambio aprobado.
 * Soporta: Módulo de Gestión de Cambios - Versionado.
 */
@Entity
@Table(name = "requisitos_versiones", uniqueConstraints = {
        @UniqueConstraint(name = "uk_requisito_version",
                columnNames = {"requisito_id", "version"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitoVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisito_id", nullable = false)
    private Requisito requisito;

    @Column(nullable = false, length = 10)
    private String version;

    @Column(name = "snapshot_json", nullable = false, columnDefinition = "TEXT")
    private String snapshotJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_request_id")
    private ChangeRequest changeRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
