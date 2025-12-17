package um.backend.selection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import um.backend.proxy.ProxyClient;
import um.backend.selection.dto.SelectionDtos.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SelectionController {

    private final SelectionService service;
    private final ProxyClient proxy;
    private final ObjectMapper mapper;

    public SelectionController(SelectionService service, ProxyClient proxy, ObjectMapper mapper) {
        this.service = service;
        this.proxy = proxy;
        this.mapper = mapper;
    }

    @PostMapping("/selection")
    public Mono<ResponseEntity<SelectionResponse>> createOrGet(@RequestBody CreateRequest req) {
        return Mono.fromCallable(() -> service.getOrCreate(req.userId, req.eventoId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/selection/seats")
    public Mono<ResponseEntity<SelectionResponse>> updateSeats(@RequestBody SeatsRequest req) {
        return Mono.fromCallable(() -> service.updateSeats(req.selectionId, req.seats))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/selection/names")
    public Mono<ResponseEntity<SelectionResponse>> updateNames(@RequestBody NamesRequest req) {
        return Mono.fromCallable(() -> service.updateNames(req.selectionId, req.names))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/selection/block")
    public Mono<ResponseEntity<SelectionResponse>> block(@RequestBody BlockRequest req) {
        return Mono.fromCallable(() -> service.getById(req.selectionId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(state -> {
                    List<Map<String, Integer>> asientos = state.getSeats().stream()
                            .map(s -> Map.of("fila", s.fila, "columna", s.columna))
                            .toList();

                    return proxy.bloquearAsientos(state.getEventoId(), asientos)
                            .flatMap(resp -> {
                                Instant hasta = service.defaultBlockTtlUntil();
                                try {
                                    if (resp.getBody() != null) {
                                        JsonNode body = mapper.readTree(resp.getBody());
                                        String ts = body.path("bloqueadoHasta").asText(body.path("expira").asText(null));
                                        if (ts != null && !ts.isBlank()) {
                                            hasta = Instant.parse(ts);
                                        }
                                    }
                                } catch (Exception ignored) {}

                                Instant finalHasta = hasta;
                                return Mono.fromCallable(() -> service.markBlocked(state.getId(), finalHasta))
                                        .subscribeOn(Schedulers.boundedElastic());
                            });
                })
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(409).build()));
    }

    private SelectionResponse toResponse(SelectionStateEntity e) {
        SelectionResponse r = new SelectionResponse();
        r.id = e.getId();
        r.userId = e.getUserId();
        r.eventoId = e.getEventoId();
        r.seats = e.getSeats();
        r.names = e.getNames();
        r.stage = e.getStage();
        r.bloqueadoHasta = e.getBloqueadoHasta();
        r.updatedAt = e.getUpdatedAt();
        return r;
    }
}