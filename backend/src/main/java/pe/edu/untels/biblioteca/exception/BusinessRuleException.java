package pe.edu.untels.biblioteca.exception;

/**
 * Se lanza cuando una operacion viola una regla de negocio
 * (stock insuficiente, prestamo duplicado, email ya registrado, etc).
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
