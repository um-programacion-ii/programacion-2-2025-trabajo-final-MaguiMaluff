package ar.edu.um.proxy.adapters.outbound.backend;

import ar.edu.um.proxy.ports.outbound.BackendNotifierPort;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter que notifica al backend (POST /api/proxy/event-change).
 * Usa backendWebClient nombrado en WebClientConfig.
 **/
@Component
public class BackendWebClientAdapter implements BackendNotifierPort {

    private final Logger log = LoggerFactory.getLogger(BackendWebClientAdapter.class);
    private final WebClient backendClient;
    private static final String EVENT_CHANGE_ENDPOINT = "/api/proxy/event-change";

    public BackendWebClientAdapter(@Qualifier("backendWebClient") WebClient backendClient) {
        this.backendClient = backendClient;
    }

    @Override
    public void notifyEventoChange(Long eventoId, String rawMensajeKafka, JsonNode eventoCompleto) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("eventoId", eventoId);
            payload.put("mensajeKafka", rawMensajeKafka);
            payload.put("evento", eventoCompleto);
            backendClient.post()
                    .uri(EVENT_CHANGE_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.debug("Notificación enviada al backend para eventoId={}", eventoId);
        } catch (Exception e) {
            log.error("Error notificando al backend: {}", e.getMessage(), e);
        }
    }
}