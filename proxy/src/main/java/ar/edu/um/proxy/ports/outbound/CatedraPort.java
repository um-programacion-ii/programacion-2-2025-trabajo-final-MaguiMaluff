package ar.edu.um.proxy.ports.outbound;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Puerto outbound: abstracción para interactuar con el servicio de la cátedra.
 * Implementado por un adaptador WebClient.
 */
public interface CatedraPort {

    // Lecturas (GET) devuelven JSON String o lanzan excepciones en caso de error (reactivo)
    Mono<String> eventosResumidos();
    Mono<String> eventos();
    Mono<String> evento(long id);
    Mono<String> ventas();
    Mono<String> venta(long id);

    // Forward POST: bloqueos y ventas; devuelven ResponseEntity con status y body del upstream (reactivo)
    Mono<ResponseEntity<String>> bloquearAsientos(String payloadJson, String bearerToken);
    Mono<ResponseEntity<String>> realizarVenta(String payloadJson, String bearerToken);

    // Forzar actualización (util)
    Mono<String> forzarActualizacion();
}