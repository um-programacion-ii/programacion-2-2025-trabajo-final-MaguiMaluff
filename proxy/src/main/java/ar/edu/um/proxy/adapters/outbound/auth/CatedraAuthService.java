package ar.edu.um.proxy.adapters.outbound.auth;

import ar.edu.um.proxy.config.ProxyProperties;
import ar.edu.um.proxy.service.TokenService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Servicio que hace login en la cátedra (/api/authenticate) con las credenciales configuradas
 * y actualiza TokenService en memoria. Ejecuta al inicio y periódicamente (reactivo).
 */
@Service
public class CatedraAuthService {

    private static final Logger log = LoggerFactory.getLogger(CatedraAuthService.class);
    private final WebClient catedraClient;
    private final ProxyProperties props;
    private final TokenService tokens;
    private final ObjectMapper mapper = new ObjectMapper();

    public CatedraAuthService(@Qualifier("catedraWebClient") WebClient catedraClient,
                              ProxyProperties props,
                              TokenService tokens) {
        this.catedraClient = catedraClient;
        this.props = props;
        this.tokens = tokens;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        loginReactive().subscribeOn(Schedulers.boundedElastic()).subscribe(
                v -> {},
                e -> log.warn("Login inicial falló: {}", e.getMessage())
        );
    }

    @Scheduled(fixedDelayString = "${proxy.catedra.refresh-interval-ms:1800000}")
    public void scheduledRefresh() {
        loginReactive().subscribeOn(Schedulers.boundedElastic()).subscribe(
                v -> {},
                e -> log.warn("Login programado falló: {}", e.getMessage())
        );
    }

    public Mono<Void> loginReactive() {
        String user = props.getCatedra().getUsername();
        String pass = props.getCatedra().getPassword();
        if (user == null || pass == null) {
            log.warn("No hay credenciales de cátedra configuradas (proxy.catedra.username/password)");
            return Mono.empty();
        }
        return catedraClient.post()
                .uri("/api/authenticate")
                .bodyValue(mapper.createObjectNode()
                        .put("username", user)
                        .put("password", pass)
                        .put("rememberMe", false))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> {
                    if (body == null || body.isBlank()) {
                        log.warn("Respuesta vacía del login a cátedra");
                        return Mono.empty();
                    }
                    try {
                        JsonNode n = mapper.readTree(body);
                        String token = null;
                        if (n.has("id_token")) token = n.get("id_token").asText(null);
                        else if (n.has("token")) token = n.get("token").asText(null);

                        if (token != null && !token.isBlank()) {
                            tokens.update(token);
                            log.info("Login a cátedra exitoso (usuario={})", user);
                        } else {
                            log.warn("Login a cátedra no devolvió token: {}", body);
                        }
                    } catch (Exception e) {
                        log.warn("Error parseando login de cátedra: {}", e.getMessage());
                    }
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.warn("Error logeando contra cátedra: {}", e.getMessage());
                    return Mono.empty();
                })
                .then();
    }
}