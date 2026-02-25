package ar.edu.um.proxy.exception;

/** Excepción para recursos no encontrados (404). */
public class ProxyNotFoundException extends RuntimeException {
    public ProxyNotFoundException(String message) { super(message); }
}