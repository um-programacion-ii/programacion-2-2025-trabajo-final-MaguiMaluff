package ar.edu.um.proxy.ports.outbound;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Puerto para notificar al backend cuando hay cambios (triggered por Kafka).
 */
public interface BackendNotifierPort {
    void notifyEventoChange(Long eventoId, String rawMensajeKafka, JsonNode eventoCompleto);
}