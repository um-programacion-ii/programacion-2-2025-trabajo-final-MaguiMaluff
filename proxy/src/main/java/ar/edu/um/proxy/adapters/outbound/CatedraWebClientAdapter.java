package ar.edu.um.proxy.adapters.outbound;

import ar.edu.um.proxy.adapters.outbound.auth.CatedraAuthService;
import ar.edu.um.proxy.ports.outbound.CatedraPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Implementación del puerto CatedraPort usando WebClient en modo reactivo.
 * - Agrega retry único en 401: reintenta tras refrescar el login.
 */
@Component
public class CatedraWebClientAdapter implements CatedraPort {

    private static final Logger log = LoggerFactory.getLogger(CatedraWebClientAdapter.class);
    private final WebClient client;
    private final CatedraAuthService authService;

    public CatedraWebClientAdapter(@Qualifier("catedraWebClient") WebClient client,
                                   CatedraAuthService authService) {
        this.client = client;
        this.authService = authService;
    }

    private Mono<String> doGet(String path) {
        return doGet(path, false);
    }

    private Mono<String> doGet(String path, boolean retried) {
        return client.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.Unauthorized.class, wre -> {
                    if (retried) return Mono.error(wre);
                    log.info("401 en GET {}, intentando relogin y reintento único…", path);
                    return authService.loginReactive().then(doGet(path, true));
                })
                .doOnError(WebClientResponseException.class, wre ->
                        log.warn("Upstream GET {} returned status {}: {}", path, wre.getRawStatusCode(), wre.getResponseBodyAsString()))
                .doOnError(e -> log.error("Error realizando GET a cátedra {}: {}", path, e.getMessage(), e));
    }

    @Override
    public Mono<String> eventosResumidos() {
        return doGet("/api/endpoints/v1/eventos-resumidos");
    }

    @Override
    public Mono<String> eventos() {
        return doGet("/api/endpoints/v1/eventos");
    }

    @Override
    public Mono<String> evento(long id) {
        return doGet("/api/endpoints/v1/evento/" + id);
    }

    @Override
    public Mono<String> ventas() {
        return doGet("/api/endpoints/v1/listar-ventas");
    }

    @Override
    public Mono<String> venta(long id) {
        return doGet("/api/endpoints/v1/listar-venta/" + id);
    }

    @Override
    public Mono<ResponseEntity<String>> bloquearAsientos(String payloadJson, String bearerToken) {
        return client.post()
                .uri("/api/endpoints/v1/bloquear-asientos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payloadJson)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.Unauthorized.class, wre -> {
                    log.info("401 en POST bloquear-asientos, intentando relogin y reintento único…");
                    return authService.loginReactive().then(
                            client.post()
                                    .uri("/api/endpoints/v1/bloquear-asientos")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(payloadJson)
                                    .retrieve()
                                    .toEntity(String.class)
                    );
                })
                .doOnError(WebClientResponseException.class, wre ->
                        log.warn("Bloquear asientos upstream falló: status={}, body={}", wre.getRawStatusCode(), wre.getResponseBodyAsString()))
                .doOnError(e -> log.error("Error reenviando bloqueo a cátedra: {}", e.getMessage(), e));
    }

    @Override
    public Mono<ResponseEntity<String>> realizarVenta(String payloadJson, String bearerToken) {
        return client.post()
                .uri("/api/endpoints/v1/realizar-venta")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payloadJson)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.Unauthorized.class, wre -> {
                    log.info("401 en POST realizar-venta, intentando relogin y reintento único…");
                    return authService.loginReactive().then(
                            client.post()
                                    .uri("/api/endpoints/v1/realizar-venta")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(payloadJson)
                                    .retrieve()
                                    .toEntity(String.class)
                    );
                })
                .doOnError(WebClientResponseException.class, wre ->
                        log.warn("Realizar venta upstream falló: status={}, body={}", wre.getRawStatusCode(), wre.getResponseBodyAsString()))
                .doOnError(e -> log.error("Error reenviando venta a cátedra: {}", e.getMessage(), e));
    }

    @Override
    public Mono<String> forzarActualizacion() {
        return doGet("/api/endpoints/v1/forzar-actualizacion");
    }
}