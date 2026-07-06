package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "validation_session_reqs", uniqueConstraints = {
    @UniqueConstraint(name = "uk_val_sesion_req", columnNames = {"sesion_id", "requisito_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationSessionReq {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private ValidationSession sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisito_id", nullable = false)
    private Requisito requisito;

    @Column(name = "estado_validacion", nullable = false, length = 20)
    @Builder.Default
    private String estadoValidacion = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
