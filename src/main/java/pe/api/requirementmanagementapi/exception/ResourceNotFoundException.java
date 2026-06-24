package pe.api.requirementmanagementapi.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra.
 * Resulta en HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String recurso, String campo, Object valor) {
        super(String.format("%s no encontrado con %s: '%s'", recurso, campo, valor));
    }
}
