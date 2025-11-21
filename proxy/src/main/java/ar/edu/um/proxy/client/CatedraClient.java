package ar.edu.um.proxy.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CatedraClient {

    private final Logger log = LoggerFactory.getLogger(CatedraClient.class);
    private final WebClient webClient;
    private final String token;

    public CatedraClient(WebClient webClient,
                         @Value("${CATEDRA_TOKEN:}") String token) {
        this.webClient = webClient;
        this.token = token;
    }

    public String getEvento(Long id) {
        String path = "/api/endpoints/v1/evento/" + id;
        try {
            WebClient.RequestHeadersSpec<?> req = webClient.get().uri(path)
                    .accept(MediaType.APPLICATION_JSON);
            if (token != null && !token.isEmpty()) {
                req = req.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
            return req.retrieve().bodyToMono(String.class).block();
        } catch (Exception e) {
            log.warn("Error obteniendo evento {} de cátedra: {}", id, e.getMessage());
            return null;
        }
    }
}