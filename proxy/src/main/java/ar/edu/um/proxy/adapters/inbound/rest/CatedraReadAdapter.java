package ar.edu.um.proxy.adapters.inbound.rest;

import ar.edu.um.proxy.ports.outbound.CatedraPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Controlador de lectura reactivo que expone los endpoints de la cátedra a través del proxy.
 */
@RestController
@RequestMapping("/proxy")
public class CatedraReadAdapter {

    private final Logger log = LoggerFactory.getLogger(CatedraReadAdapter.class);
    private final CatedraPort catedra;

    public CatedraReadAdapter(CatedraPort catedra) {
        this.catedra = catedra;
    }

    @GetMapping(value = "/eventos-resumidos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> eventosResumidos() {
        return call(catedra.eventosResumidos(), "eventos-resumidos");
    }

    @GetMapping(value = "/eventos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> eventos() {
        return call(catedra.eventos(), "eventos");
    }

    @GetMapping(value = "/evento/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> evento(@PathVariable long id) {
        return call(catedra.evento(id), "evento " + id);
    }

    @GetMapping(value = "/ventas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> ventas() {
        return call(catedra.ventas(), "ventas");
    }

    @GetMapping(value = "/venta/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> venta(@PathVariable long id) {
        return call(catedra.venta(id), "venta " + id);
    }

    @GetMapping(value = "/forzar-actualizacion", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> forzarActualizacion() {
        return call(catedra.forzarActualizacion(), "forzar-actualizacion");
    }

    private Mono<ResponseEntity<String>> call(Mono<String> op, String nombre) {
        return op
                .map(body -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body == null ? "" : body))
                .onErrorResume(WebClientResponseException.class, wre -> {
                    int status = wre.getRawStatusCode();
                    String body = wre.getResponseBodyAsString();
                    log.warn("Cátedra {} returned status {}: {}", nombre, status, body);
                    return Mono.just(ResponseEntity
                            .status(status)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(body));
                })
                .onErrorResume(e -> {
                    log.warn("Error {}: {}", nombre, e.getMessage(), e);
                    return Mono.just(ResponseEntity
                            .status(502)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("{\"error\":\"upstream error\"}"));
                });
    }
}