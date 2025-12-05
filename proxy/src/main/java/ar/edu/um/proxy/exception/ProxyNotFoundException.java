package ar.edu.um.proxy.exception;

/*
 * Excepción para representar recursos no encontrados desde el proxy (por ejemplo, datos en Redis).
 * - Usada por ProxyController.getAsientos para indicar que no se encontraron asientos.
 * - RestExceptionHandler mapea esta excepción a NOT_FOUND (404).
 */
public class ProxyNotFoundException extends ProxyException {
    public ProxyNotFoundException(String message) { super(message); }
}