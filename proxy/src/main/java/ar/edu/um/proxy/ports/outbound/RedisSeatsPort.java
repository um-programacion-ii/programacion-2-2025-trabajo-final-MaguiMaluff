package ar.edu.um.proxy.ports.outbound;

/**
 * Puerto outbound para Redis: lectura/escritura de la key evento_{id} (JSON crudo).
 */
public interface RedisSeatsPort {
    String readAsientosRaw(Long eventoId);
    void writeAsientosRaw(Long eventoId, String json);
}