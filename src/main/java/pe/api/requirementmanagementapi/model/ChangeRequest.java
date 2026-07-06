package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;
import pe.api.requirementmanagementapi.model.enums.EstadoChangeRequest;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

/**
 * Entidad que representa una Solicitud de Cambio (Change Request).
 * Mapeada a la tabla 'change_requests' de PostgreSQL.
 * Flujo: PROPUESTO -> ANALIZADO -> APROBADO/RECHAZADO
 * Soporta: Módulo de Gestión de Cambios - CCB.
 */
@Entity
@Table(name = "change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisito_id", nullable = false)
    private Requisito requisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justificacion;

    @Column(name = "texto_propuesto", columnDefinition = "TEXT")
    private String textoPropuesto;

    @Column(name = "impacto_tecnico", columnDefinition = "TEXT")
    private String impactoTecnico;

    @Column(name = "impacto_negocio", columnDefinition = "TEXT")
    private String impactoNegocio;

    @Column(columnDefinition = "TEXT")
    private String riesgos;

    @Column(name = "esfuerzo_estimado", length = 50)
    private String esfuerzoEstimado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoChangeRequest estado = EstadoChangeRequest.PROPUESTO;

    @Column(name = "probabilidad_riesgo", precision = 5, scale = 2)
    private BigDecimal probabilidadRiesgo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisado_por_id")
    private Usuario revisadoPor;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
