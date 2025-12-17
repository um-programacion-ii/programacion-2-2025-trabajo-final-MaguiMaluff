package um.backend.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import um.backend.proxy.ProxyClient;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class EventSyncService {

    private final ProxyClient proxy;
    private final EventRepository repo;
    private final ObjectMapper mapper;

    public EventSyncService(ProxyClient proxy, EventRepository repo, ObjectMapper mapper) {
        this.proxy = proxy;
        this.repo = repo;
        this.mapper = mapper;
    }

    // Sincroniza todos los eventos desde /proxy/eventos
    public Mono<Integer> syncAllFromProxy() {
        return proxy.eventos()
                .flatMap(json -> Mono.fromCallable(() -> {
                                    JsonNode root = mapper.readTree(json);
                                    int count = 0;
                                    if (root.isArray()) {
                                        for (JsonNode e : root) {
                                            upsertOne(e);
                                            count++;
                                        }
                                    }
                                    return count;
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                );
    }

    // Sincroniza un evento por ID desde /proxy/evento/{id}
    public Mono<Boolean> syncOne(long eventId) {
        return proxy.evento(eventId)
                .flatMap(json -> Mono.fromCallable(() -> {
                            JsonNode node = mapper.readTree(json);
                            upsertOne(node);
                            return true;
                        }).subscribeOn(Schedulers.boundedElastic())
                );
    }

    @Transactional
    protected void upsertOne(JsonNode e) {
        Long id = e.path("id").asLong();
        EventEntity entity = repo.findById(id).orElseGet(EventEntity::new);
        entity.setId(id);
        entity.setTitulo(text(e, "titulo"));
        entity.setResumen(text(e, "resumen"));
        entity.setDescripcion(text(e, "descripcion"));
        entity.setFecha(instant(e, "fecha"));
        entity.setDireccion(text(e, "direccion"));
        entity.setImagen(text(e, "imagen"));
        entity.setFilaAsientos(intOrNull(e, "filaAsientos"));
        entity.setColumnAsientos(intOrNull(e, "columnAsientos"));
        entity.setPrecioEntrada(decimal(e, "precioEntrada"));
        JsonNode tipo = e.path("eventoTipo");
        if (tipo != null && !tipo.isMissingNode() && tipo.isObject()) {
            entity.setTipoNombre(text(tipo, "nombre"));
            entity.setTipoDescripcion(text(tipo, "descripcion"));
        }
        repo.save(entity);
    }

    private String text(JsonNode n, String field) {
        return n.hasNonNull(field) ? n.get(field).asText() : null;
    }

    private Integer intOrNull(JsonNode n, String field) {
        return n.hasNonNull(field) ? n.get(field).asInt() : null;
    }

    private Instant instant(JsonNode n, String field) {
        String v = text(n, field);
        if (v == null || v.isBlank()) return null;
        try { return Instant.parse(v); } catch (Exception ex) { return null; }
    }

    private BigDecimal decimal(JsonNode n, String field) {
        if (!n.hasNonNull(field)) return null;
        try {
            if (n.get(field).isNumber()) return new BigDecimal(n.get(field).asText());
            return new BigDecimal(n.get(field).asText());
        } catch (Exception ex) {
            return null;
        }
    }
}