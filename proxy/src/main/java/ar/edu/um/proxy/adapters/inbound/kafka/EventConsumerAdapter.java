package ar.edu.um.proxy.adapters.inbound.kafka;

import ar.edu.um.proxy.application.usecase.NotifyEventChangeUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ar.edu.um.proxy.config.ProxyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador inbound Kafka: escucha el tópico y delega al usecase para notificar al backend.
 * Opción de 'enrich' para pedir evento completo desde cátedra
 */
@Component
public class EventConsumerAdapter {

    private final Logger log = LoggerFactory.getLogger(EventConsumerAdapter.class);
    private final NotifyEventChangeUseCase notifier;
    private final ObjectMapper mapper = new ObjectMapper();
    private final boolean enrich;

    public EventConsumerAdapter(NotifyEventChangeUseCase notifier, ProxyProperties props) {
        this.notifier = notifier;
        this.enrich = props.getKafka().isEnrich();
    }

    @KafkaListener(topics = "${proxy.kafka.topic:eventos-actualizacion}", groupId = "${proxy.kafka.group-id:proxy-group}")
    public void consume(String raw) {
        if (raw == null) {
            log.warn("Kafka mensaje nulo recibido, se ignora");
            return;
        }
        log.info("Kafka mensaje recibido ({} bytes)", raw.length());
        Long eventoId = null;
        JsonNode rawNode = null;
        try {
            rawNode = mapper.readTree(raw);
            if (rawNode.has("eventoId")) {
                eventoId = rawNode.get("eventoId").asLong();
            }
        } catch (Exception e) {
            log.error("Error parseando mensaje Kafka: {}", e.getMessage());
        }
        JsonNode eventoCompleto = null;

        notifier.execute(eventoId, raw, eventoCompleto);
    }
}