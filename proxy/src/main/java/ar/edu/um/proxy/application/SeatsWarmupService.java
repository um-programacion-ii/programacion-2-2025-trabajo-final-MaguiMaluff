package ar.edu.um.proxy.application;

import ar.edu.um.proxy.adapters.outbound.redis.RedisSeatRepository;
import ar.edu.um.proxy.ports.outbound.CatedraPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class SeatsWarmupService {

    private final RedisSeatRepository repo;
    private final CatedraPort catedra;
    private final ObjectMapper mapper = new ObjectMapper();

    public SeatsWarmupService(RedisSeatRepository repo, CatedraPort catedra) {
        this.repo = repo;
        this.catedra = catedra;
    }

    public Mono<String> getOrWarmOverlay(long eventoId) {
        // 1) Intento leer de Redis
        return Mono.fromCallable(() -> repo.findSeatMapRaw(eventoId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.map(Mono::just).orElseGet(() ->
                        // 2) No existe → construir overlay desde "ventas"
                        catedra.ventas()
                                .map(json -> buildOverlayFromVentas(json, eventoId))
                                // Si la cátedra falla, devuelve overlay vacío para no romper clientes
                                .onErrorResume(e -> Mono.just(emptyOverlay(eventoId)))
                                // 3) Guardar
                                .flatMap(overlay -> Mono.fromCallable(() -> {
                                            boolean created = repo.setIfAbsent(eventoId, overlay);
                                            if (!created) {
                                                return repo.findSeatMapRaw(eventoId).orElse(overlay);
                                            }
                                            return overlay;
                                        }).subscribeOn(Schedulers.boundedElastic())
                                )
                ));
    }

    private String buildOverlayFromVentas(String ventasJson, long eventoId) {
        try {
            JsonNode root = mapper.readTree(ventasJson);

            // El JSON de "ventas" puede venir como array o envuelto;
            // - Array en la raíz
            // - Objeto con campo "ventas" (array)
            ArrayNode ventasArray;
            if (root.isArray()) {
                ventasArray = (ArrayNode) root;
            } else if (root.has("ventas") && root.get("ventas").isArray()) {
                ventasArray = (ArrayNode) root.get("ventas");
            } else {
                ventasArray = mapper.createArrayNode();
            }

            Set<String> dedupe = new HashSet<>();
            ArrayNode asientos = mapper.createArrayNode();

            for (JsonNode v : ventasArray) {
                // Intentamos ubicar el ID de evento de varias formas habituales
                long evId = v.path("eventoId").asLong(
                        v.at("/evento/id").asLong(
                                v.path("evento").asLong(-1)
                        )
                );
                if (evId != eventoId) continue;

                // Extraer fila/columna; la venta puede tener:
                // - Campos directos "fila" y "columna"
                // - Un array "asientos" o "entradas" con objetos que tienen "fila"/"columna"
                if (v.hasNonNull("fila") && v.hasNonNull("columna")) {
                    int fila = v.get("fila").asInt();
                    int col = v.get("columna").asInt();
                    addVendido(asientos, dedupe, fila, col);
                } else {
                    ArrayNode detalles = null;
                    if (v.has("asientos") && v.get("asientos").isArray()) {
                        detalles = (ArrayNode) v.get("asientos");
                    } else if (v.has("entradas") && v.get("entradas").isArray()) {
                        detalles = (ArrayNode) v.get("entradas");
                    }
                    if (detalles != null) {
                        for (JsonNode d : detalles) {
                            if (d.hasNonNull("fila") && d.hasNonNull("columna")) {
                                int fila = d.get("fila").asInt();
                                int col = d.get("columna").asInt();
                                addVendido(asientos, dedupe, fila, col);
                            }
                        }
                    }
                }
            }

            ObjectNode overlay = mapper.createObjectNode();
            overlay.put("eventoId", eventoId);
            overlay.set("asientos", asientos);
            return mapper.writeValueAsString(overlay);
        } catch (Exception e) {
            // Si no podemos interpretar, devolvemos overlay vacío
            return emptyOverlay(eventoId);
        }
    }

    private void addVendido(ArrayNode asientos, Set<String> dedupe, int fila, int col) {
        String key = fila + "-" + col;
        if (dedupe.add(key)) {
            ObjectNode seat = asientos.addObject();
            seat.put("fila", fila);
            seat.put("columna", col);
            seat.put("estado", "Vendido");
        }
    }

    private String emptyOverlay(long eventoId) {
        return "{\"eventoId\":" + eventoId + ",\"asientos\":[]}";
    }
}