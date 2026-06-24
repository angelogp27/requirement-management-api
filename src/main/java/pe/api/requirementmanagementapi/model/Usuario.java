package pe.api.requirementmanagementapi.model;

import jakarta.persistence.*;
import lombok.*;
import pe.api.requirementmanagementapi.model.enums.Rol;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un usuario del sistema REM.
 * Mapeada a la tabla 'usuarios' de PostgreSQL.
 * Roles: ANALISTA, JEFE_PROYECTO, TECNICO (ERS sección 2.3).
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_ultima_conexion")
    private LocalDateTime fechaUltimaConexion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
