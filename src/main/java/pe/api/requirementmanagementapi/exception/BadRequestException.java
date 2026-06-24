package pe.api.requirementmanagementapi.exception;

/**
 * Excepción lanzada para validaciones de negocio fallidas.
 * Resulta en HTTP 400 Bad Request.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
