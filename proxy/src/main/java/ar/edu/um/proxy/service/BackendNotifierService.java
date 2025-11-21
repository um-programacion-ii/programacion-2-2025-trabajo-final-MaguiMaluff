package ar.edu.um.proxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class BackendNotifierService {

    private final Logger log = LoggerFactory.getLogger(BackendNotifierService.class);
    private final WebClient webClient;
    private final String backendNotifyUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public BackendNotifierService(@Value("${PROXY_BACKEND_NOTIFY_URL:}") String backendNotifyUrl) {
        this.backendNotifyUrl = backendNotifyUrl;
        this.webClient = WebClient.builder().build();
    }

    public void notifyEventoChange(Long eventoId, String rawKafkaMessage, JsonNode enrichedEvento) {
        if (backendNotifyUrl == null || backendNotifyUrl.isEmpty()) {
            log.warn("PROXY_BACKEND_NOTIFY_URL no configurada, se omite notificación eventoId={}", eventoId);
            return;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("eventoId", eventoId);
            body.put("kafkaPayload", mapper.readTree(rawKafkaMessage));
            if (enrichedEvento != null) {
                body.put("eventoCompleto", enrichedEvento);
            }
            webClient.post()
                    .uri(backendNotifyUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)).maxBackoff(Duration.ofSeconds(10)))
                    .doOnError(err -> log.error("Error notificando backend eventoId {}: {}", eventoId, err.getMessage()))
                    .doOnSuccess(resp -> log.debug("Backend notificado OK eventoId {} resp={}", eventoId, resp))
                    .subscribe();
        } catch (Exception e) {
            log.error("Fallo preparando notificación eventoId {}: {}", eventoId, e.getMessage(), e);
        }
    }
}