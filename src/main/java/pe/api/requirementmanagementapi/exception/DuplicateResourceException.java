package pe.api.requirementmanagementapi.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso duplicado.
 * Resulta en HTTP 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String recurso, String campo, Object valor) {
        super(String.format("%s ya existe con %s: '%s'", recurso, campo, valor));
    }
}
