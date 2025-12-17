package um.backend.proxy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class ProxyClient {
    private final WebClient proxy;

    public ProxyClient(@Qualifier("proxyWebClient") WebClient proxy) {
        this.proxy = proxy;
    }

    public Mono<String> eventos() {
        return proxy.get().uri("/proxy/eventos")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> evento(long id) {
        return proxy.get().uri("/proxy/evento/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> asientosRaw(long eventoId) {
        return proxy.get().uri("/proxy/asientos/{id}", eventoId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }

    // Bloqueo de asientos a través del proxy
    public Mono<ResponseEntity<String>> bloquearAsientos(long eventoId, List<Map<String,Integer>> asientos) {
        Map<String, Object> payload = Map.of(
                "eventoId", eventoId,
                "asientos", asientos
        );
        return proxy.post()
                .uri("/proxy/bloquear")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toEntity(String.class);
    }
}