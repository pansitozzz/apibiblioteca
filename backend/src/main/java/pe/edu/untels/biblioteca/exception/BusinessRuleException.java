package pe.edu.untels.biblioteca.exception;

/**
 * Se lanza cuando una operación viola una regla de negocio
 * (stock insuficiente, préstamo duplicado, email ya registrado, etc).
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
