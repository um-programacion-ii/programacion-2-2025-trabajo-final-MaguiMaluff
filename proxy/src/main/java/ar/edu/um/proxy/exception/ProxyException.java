package ar.edu.um.proxy.exception;

/*
 * Excepción base para errores del proxy.
 * - Extiende RuntimeException para que pueda ser lanzada sin necesidad de declaración throws.
 * - Se usa como clase padre para excepciones específicas (ej. ProxyNotFoundException).
 * - RestExceptionHandler maneja ProxyException y devuelve BAD_REQUEST por defecto.
 */
public class ProxyException extends RuntimeException {
    public ProxyException() { super(); }
    public ProxyException(String message) { super(message); }
    public ProxyException(String message, Throwable cause) { super(message, cause); }
    public ProxyException(Throwable cause) { super(cause); }
}