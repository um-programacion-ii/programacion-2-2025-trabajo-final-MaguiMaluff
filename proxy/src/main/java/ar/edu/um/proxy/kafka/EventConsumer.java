package ar.edu.um.proxy.kafka;

import ar.edu.um.proxy.client.CatedraClient;
import ar.edu.um.proxy.service.BackendNotifierService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private final Logger log = LoggerFactory.getLogger(EventConsumer.class);
    private final BackendNotifierService notifier;
    private final CatedraClient catedraClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final boolean enrich;

    public EventConsumer(BackendNotifierService notifier,
                         CatedraClient catedraClient,
                         @Value("${proxy.kafka.enrich:false}") boolean enrich) {
        this.notifier = notifier;
        this.catedraClient = catedraClient;
        this.enrich = enrich;
    }

    @KafkaListener(topics = "${proxy.kafka.topic:eventos-actualizacion}", groupId = "${proxy.kafka.groupId:proxy-group}")
    public void consume(String raw) {
        log.info("Kafka mensaje recibido ({} bytes)", raw.length());
        Long eventoId = null;
        JsonNode node = null;
        try {
            node = mapper.readTree(raw);
            if (node.has("eventoId")) {
                eventoId = node.get("eventoId").asLong();
            }
        } catch (Exception e) {
            log.error("Error parseando mensaje Kafka: {}", e.getMessage());
        }

        JsonNode eventoCompletoNode = null;
        if (enrich && eventoId != null) {
            String eventoJson = catedraClient.getEvento(eventoId);
            if (eventoJson != null) {
                try {
                    eventoCompletoNode = mapper.readTree(eventoJson);
                } catch (Exception ignored) {}
            }
        }
        notifier.notifyEventoChange(eventoId, raw, eventoCompletoNode);
    }
}