package ar.edu.um.proxy.service;

import ar.edu.um.proxy.config.ProxyProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

/*
 * BackendNotifierService
 *
 * - Servicio que notifica al backend cuando hay cambios de eventos (disparados por Kafka).
 * - Usa proxy.backend.base-url para construir el RestClient hacia el backend.
 * - ENV: PROXY_BACKEND_BASE_URL para cambiar el destino sin tocar código.
 *
 * Nota:
 * - Payload incluye: eventoId, mensajeKafka (raw) y evento completo (si enriquecido).
 */
@Service
public class BackendNotifierService {

    private static final Logger log = LoggerFactory.getLogger(BackendNotifierService.class);

    private final RestClient client;

    private static final String EVENT_CHANGE_ENDPOINT = "/api/proxy/event-change";

    public BackendNotifierService(ProxyProperties properties) {
        String baseUrl = properties.getBackend().getBaseUrl();
        this.client = RestClient.builder().baseUrl(baseUrl).build();
        log.info("BackendNotifierService apuntando a {}", baseUrl);
    }

    public void notifyEventoChange(Long eventoId, String rawMensajeKafka, JsonNode eventoCompleto) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("eventoId", eventoId);
            payload.put("mensajeKafka", rawMensajeKafka);
            payload.put("evento", eventoCompleto); // puede ser null

            ResponseEntity<Void> resp = client.post()
                    .uri(EVENT_CHANGE_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.debug("Notificación enviada al backend: status={}", resp.getStatusCode().value());
        } catch (Exception e) {
            log.error("Error notificando cambio al backend: {}", e.getMessage(), e);
        }
    }
}