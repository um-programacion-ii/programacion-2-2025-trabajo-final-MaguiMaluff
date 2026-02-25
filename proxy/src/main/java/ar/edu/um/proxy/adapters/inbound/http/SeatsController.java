package ar.edu.um.proxy.adapters.inbound.http;

import ar.edu.um.proxy.application.SeatsWarmupService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/proxy")
public class SeatsController {

    private final SeatsWarmupService warmup;

    public SeatsController(SeatsWarmupService warmup) {
        this.warmup = warmup;
    }

    @GetMapping(value = "/asientos/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> getSeatsRaw(@PathVariable("id") long id) {
        return warmup.getOrWarmOverlay(id)
                .map(json -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"warmup_error\"}")));
    }
}