package ar.edu.um.proxy.application.usecase;

import ar.edu.um.proxy.ports.outbound.BackendNotifierPort;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Caso de uso que notifica al backend cuando hay un cambio en eventos (triggered por Kafka).
 * Recibe el mensaje raw de Kafka y el evento completo.
 */
@Service
public class NotifyEventChangeUseCase {

    private final Logger log = LoggerFactory.getLogger(NotifyEventChangeUseCase.class);
    private final BackendNotifierPort backendNotifier;

    public NotifyEventChangeUseCase(BackendNotifierPort backendNotifier) {
        this.backendNotifier = backendNotifier;
    }

    public void execute(Long eventoId, String rawMensajeKafka, JsonNode eventoCompleto) {
        backendNotifier.notifyEventoChange(eventoId, rawMensajeKafka, eventoCompleto);
    }
}