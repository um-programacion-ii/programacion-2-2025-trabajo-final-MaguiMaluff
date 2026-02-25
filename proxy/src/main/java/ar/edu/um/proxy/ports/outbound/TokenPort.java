package ar.edu.um.proxy.ports.outbound;

/**
 * Puerto para manejar el token usado con la cátedra.
 */
public interface TokenPort {
    String current();
    void update(String token);
}