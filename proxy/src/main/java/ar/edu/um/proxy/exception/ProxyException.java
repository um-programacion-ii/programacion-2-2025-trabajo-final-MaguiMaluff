package ar.edu.um.proxy.exception;

/** Excepción genérica para errores del proxy. */
public class ProxyException extends RuntimeException {
    public ProxyException(String message) { super(message); }
    public ProxyException(String message, Throwable t) { super(message, t); }
}