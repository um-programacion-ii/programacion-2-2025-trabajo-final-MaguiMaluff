package um.backend.selection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import um.backend.proxy.ProxyClient;
import um.backend.sales.SaleEntity;
import um.backend.sales.SalesService;
import um.backend.selection.dto.ConfirmDtos;
import um.backend.selection.dto.SelectionDtos.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SelectionController {

    private final SelectionService service;
    private final ProxyClient proxy;
    private final SalesService sales;
    private final ObjectMapper mapper;

    // FIX: inyectar SalesService correctamente
    public SelectionController(SelectionService service, ProxyClient proxy, SalesService sales, ObjectMapper mapper) {
        this.service = service;
        this.proxy = proxy;
        this.sales = sales;
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

    @PostMapping("/selection/confirm")
    public Mono<ResponseEntity<ConfirmDtos.ConfirmResponse>> confirm(@RequestBody ConfirmDtos.ConfirmRequest req) {
        return Mono.fromCallable(() -> service.getById(req.selectionId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(state -> {
                    Instant now = Instant.now();

                    // Validaciones
                    if (!sales.isBlockedAndValid(state, now)) {
                        return Mono.just(ResponseEntity.status(409)
                                .body(error("Selección no bloqueada o vencida")));
                    }
                    if (state.getSeats() == null || state.getSeats().isEmpty()) {
                        return Mono.just(ResponseEntity.status(409)
                                .body(error("No hay asientos seleccionados")));
                    }
                    if (state.getNames() == null || state.getNames().isEmpty()
                            || state.getNames().size() != state.getSeats().size()) {
                        return Mono.just(ResponseEntity.status(409)
                                .body(error("La cantidad de nombres no coincide con los asientos")));
                    }

                    // Construir payload mínimo para el proxy
                    List<Map<String, Object>> entradas = new ArrayList<>();
                    for (int i = 0; i < state.getSeats().size(); i++) {
                        var s = state.getSeats().get(i);
                        var n = state.getNames().get(i);
                        entradas.add(Map.of("fila", s.fila, "columna", s.columna, "nombre", n));
                    }

                    return proxy.realizarVenta(state.getEventoId(), entradas)
                            .flatMap(resp -> {
                                String body = resp.getBody();
                                String externalId = null;
                                BigDecimal total = null;
                                try {
                                    if (body != null && !body.isBlank()) {
                                        JsonNode node = mapper.readTree(body);
                                        externalId = textOrNull(node, "ventaId");
                                        if (externalId == null) externalId = textOrNull(node, "id");
                                        if (externalId == null) externalId = textOrNull(node, "numero");
                                        String totalStr = textOrNull(node, "total");
                                        if (totalStr == null) totalStr = textOrNull(node, "monto");
                                        if (totalStr != null) total = new BigDecimal(totalStr);
                                    }
                                } catch (Exception ignored) {}

                                BigDecimal finalTotal = total;
                                String finalExternalId = externalId;

                                // FIX: usar lambda en vez de método reference para evitar tipo Object
                                return Mono.fromCallable(() -> sales.createFromSelection(state, finalExternalId, finalTotal))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .map(sale -> toConfirmResponse(sale))
                                        .map(ResponseEntity::ok);
                            });
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(502).build()));
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

    private ConfirmDtos.ConfirmResponse toConfirmResponse(SaleEntity sale) {
        ConfirmDtos.ConfirmResponse r = new ConfirmDtos.ConfirmResponse();
        r.saleId = sale.getId();
        r.externalSaleId = sale.getExternalSaleId();
        r.eventoId = sale.getEventoId();
        r.userId = sale.getUserId();
        r.total = sale.getTotalAmount();
        r.createdAt = sale.getCreatedAt();
        r.items = sale.getItems().stream().map(i -> {
            ConfirmDtos.ConfirmResponse.Item it = new ConfirmDtos.ConfirmResponse.Item();
            it.fila = i.getFila();
            it.columna = i.getColumna();
            it.nombre = i.getNombre();
            return it;
        }).toList();
        return r;
    }

    private ConfirmDtos.ConfirmResponse error(String message) {
        ConfirmDtos.ConfirmResponse r = new ConfirmDtos.ConfirmResponse();
        r.externalSaleId = "ERROR: " + message;
        return r;
    }

    private String textOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}