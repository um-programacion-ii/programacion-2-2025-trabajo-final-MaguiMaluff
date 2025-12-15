package ar.edu.um.proxy.ports.outbound;

import org.springframework.http.ResponseEntity;

/**
 * Puerto outbound: abstracción para interactuar con el servicio de la cátedra.
 * Implementado por un adaptador WebClient.
 */
public interface CatedraPort {

    // Lecturas (GET) devuelven JSON String o lanzan excepciones en caso de error
    String eventosResumidos() throws Exception;
    String eventos() throws Exception;
    String evento(long id) throws Exception;
    String ventas() throws Exception;
    String venta(long id) throws Exception;

    // Forward POST: bloqueos y ventas; devuelven ResponseEntity con status y body del upstream
    ResponseEntity<String> bloquearAsientos(String payloadJson, String bearerToken) throws Exception;
    ResponseEntity<String> realizarVenta(String payloadJson, String bearerToken) throws Exception;

    // Forzar actualización (util)
    String forzarActualizacion() throws Exception;
}