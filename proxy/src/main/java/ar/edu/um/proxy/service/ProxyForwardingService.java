package ar.edu.um.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProxyForwardingService {

    private final Logger log = LoggerFactory.getLogger(ProxyForwardingService.class);
    private final WebClient webClient;
    private final String catedraToken;

    private static final String BLOQUEAR_PATH = "/api/endpoints/v1/bloquear-asientos";
    private static final String VENTA_PATH = "/api/endpoints/v1/realizar-venta";

    public ProxyForwardingService(WebClient webClient,
                                  @Value("${CATEDRA_TOKEN:}") String catedraToken) {
        // webClient viene de WebClientConfig con baseUrl ya seteado (verifica tu bean)
        this.webClient = webClient;
        this.catedraToken = catedraToken;
    }

    public ResponseEntity<String> forwardBloquear(String payload) {
        return postForward(BLOQUEAR_PATH, payload);
    }

    public ResponseEntity<String> forwardVenta(String payload) {
        return postForward(VENTA_PATH, payload);
    }

    private ResponseEntity<String> postForward(String path, String payload) {
        log.debug("Forward {} ({} bytes) a cátedra. Token presente: {}", path, payload == null ? 0 : payload.length(),
                (catedraToken != null && !catedraToken.isEmpty()));
        WebClient.RequestBodySpec req = webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON);
        if (catedraToken != null && !catedraToken.isEmpty()) {
            req = req.header(HttpHeaders.AUTHORIZATION, "Bearer " + catedraToken);
        }
        return req.bodyValue(payload)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}