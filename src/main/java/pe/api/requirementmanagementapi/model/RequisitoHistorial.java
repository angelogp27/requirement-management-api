package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un registro de auditoría de cambios en requisitos.
 * Mapeada a la tabla 'requisitos_historial' de PostgreSQL.
 * Los registros son inmutables (insert-only) según RNFB-029.
 * Soporta: RFB-027 a RFB-031 (Gestión de Cambios) y RF-004.
 */
@Entity
@Table(name = "requisitos_historial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisito_id", nullable = false)
    private Requisito requisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_cambio", nullable = false, length = 10)
    private String tipoCambio;

    @Column(name = "campo_modificado", length = 50)
    private String campoModificado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(name = "descripcion_cambio", columnDefinition = "TEXT")
    private String descripcionCambio;

    @Column(name = "fecha_cambio", updatable = false)
    private LocalDateTime fechaCambio;

    @PrePersist
    protected void onCreate() {
        this.fechaCambio = LocalDateTime.now();
    }
}
