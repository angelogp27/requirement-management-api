package pe.api.requirementmanagementapi.model.enums;

/**
 * Estados posibles de una Solicitud de Cambio (Change Request).
 * Flujo: PROPUESTO -> ANALIZADO -> APROBADO / RECHAZADO
 */
public enum EstadoChangeRequest {
    PROPUESTO,
    ANALIZADO,
    APROBADO,
    RECHAZADO
}
