package ar.edu.um.proxy.exception;

public class ProxyException extends RuntimeException {
    public ProxyException() { super(); }
    public ProxyException(String message) { super(message); }
    public ProxyException(String message, Throwable cause) { super(message, cause); }
    public ProxyException(Throwable cause) { super(cause); }
}