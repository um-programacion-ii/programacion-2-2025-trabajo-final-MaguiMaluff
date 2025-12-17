package um.backend.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class ProxyClient {

    private static final Logger log = LoggerFactory.getLogger(ProxyClient.class);

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
    public Mono<ResponseEntity<String>> bloquearAsientos(long eventoId, List<Map<String, Integer>> asientos) {
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

    // Venta a través del proxy
    public Mono<ResponseEntity<String>> realizarVenta(long eventoId, List<Map<String, Object>> asientos) {
        Map<String, Object> payload = Map.of(
                "eventoId", eventoId,
                "asientos", asientos // { fila, columna, nombre }
        );

        return proxy.post()
                .uri("/proxy/venta")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                // No lanza excepción en 4xx/5xx → inspeccionamos status/body
                .exchangeToMono(resp -> resp.toEntity(String.class))
                .doOnNext(re -> log.info("Proxy /venta status={} body={}",
                        re.getStatusCode().value(), re.getBody() != null ? re.getBody() : ""))
                .onErrorResume(WebClientResponseException.class, wre -> {
                    log.warn("Proxy /venta exception status={} body={}",
                            wre.getRawStatusCode(), wre.getResponseBodyAsString());
                    return Mono.just(ResponseEntity.status(wre.getRawStatusCode()).body(wre.getResponseBodyAsString()));
                })
                .doOnError(e -> log.error("Error llamando al proxy /venta: {}", e.getMessage(), e));
    }
}