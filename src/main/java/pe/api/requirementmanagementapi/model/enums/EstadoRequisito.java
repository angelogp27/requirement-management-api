package pe.api.requirementmanagementapi.model.enums;

/**
 * Estados del ciclo de vida de un requisito.
 * Flujo: REGISTRADO → EN_ANALISIS → VALIDADO → APROBADO
 * Soporta RF-004 (Gestión de Cambios).
 */
public enum EstadoRequisito {
    REGISTRADO,
    EN_ANALISIS,
    VALIDADO,
    APROBADO
}
