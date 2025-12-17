package um.backend.seats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import um.backend.events.EventDto;

import java.time.Instant;
import java.util.*;

@Service
public class SeatsService {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Construye el mapa de asientos completo usando las dimensiones del evento
     * y superpone el overlay de Redis (Bloqueado/Vendido).
     * Reglas:
     * - Libre por defecto (no listado en Redis)
     * - Bloqueado: si expira está presente y en el futuro (si expiró → se ignora)
     * - Vendido gana sobre Bloqueado
     * - Múltiples registros del mismo asiento: se elige Vendido; si ambos Bloqueado, se usa el de mayor expira
     */
    public SeatMapDto build(EventDto event, String overlayJson) {
        int filas = event.filaAsientos;
        int cols = event.columnAsientos;

        // Parse overlay
        Map<String, ObjectNode> overlay = new HashMap<>();
        try {
            JsonNode root = mapper.readTree(overlayJson);
            Instant now = Instant.now();
            for (JsonNode seat : root.withArray("asientos")) {
                int fila = seat.path("fila").asInt();
                int col = seat.path("columna").asInt();
                String estado = seat.path("estado").asText();
                String key = fila + "-" + col;

                // Filtrar bloqueos expirados
                if ("Bloqueado".equalsIgnoreCase(estado)) {
                    if (!seat.hasNonNull("expira")) continue;
                    Instant exp = Instant.parse(seat.get("expira").asText());
                    if (exp.isBefore(now)) continue;
                }

                ObjectNode current = overlay.get(key);
                if (current == null) {
                    overlay.put(key, (ObjectNode) seat);
                } else {
                    String curEstado = current.path("estado").asText();
                    if ("Vendido".equalsIgnoreCase(estado)) {
                        overlay.put(key, (ObjectNode) seat);
                    } else if ("Bloqueado".equalsIgnoreCase(estado)) {
                        if ("Vendido".equalsIgnoreCase(curEstado)) {
                            // mantener vendido
                        } else {
                            // elegir el bloqueo con mayor expira
                            Instant newExp = Instant.parse(seat.get("expira").asText());
                            Instant curExp = Instant.parse(current.get("expira").asText());
                            if (newExp.isAfter(curExp)) {
                                overlay.put(key, (ObjectNode) seat);
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // overlay vacío
        }

        // Construir DTO completo
        SeatMapDto dto = new SeatMapDto();
        dto.eventoId = event.id;
        List<SeatMapDto.Seat> seats = new ArrayList<>();

        for (int f = 1; f <= filas; f++) {
            for (int c = 1; c <= cols; c++) {
                String key = f + "-" + c;
                ObjectNode ov = overlay.get(key);
                SeatMapDto.Seat s = new SeatMapDto.Seat();
                s.fila = f;
                s.columna = c;

                if (ov == null) {
                    s.estado = "Libre";
                } else {
                    String estado = ov.path("estado").asText();
                    s.estado = estado;
                    if ("Bloqueado".equalsIgnoreCase(estado) && ov.hasNonNull("expira")) {
                        try {
                            s.expira = Instant.parse(ov.get("expira").asText());
                        } catch (Exception ignored) {}
                    }
                }
                seats.add(s);
            }
        }

        dto.asientos = seats;
        return dto;
    }
}