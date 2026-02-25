package um.backend.seats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import um.backend.events.EventDto;
import um.backend.proxy.ProxyClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/events")
public class SeatsController {
    private final ProxyClient proxy;
    private final SeatsService service;
    private final ObjectMapper mapper;

    public SeatsController(ProxyClient proxy, SeatsService service, ObjectMapper mapper) {
        this.proxy = proxy;
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}/seats")
    public Mono<ResponseEntity<SeatMapDto>> seats(@PathVariable long id) {
        Mono<EventDto> eventMono = proxy.evento(id).map(body -> {
            try {
                return mapper.readValue(body, EventDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        Mono<String> overlayMono = proxy.asientosRaw(id);

        return Mono.zip(eventMono, overlayMono)
                .map(tuple -> service.build(tuple.getT1(), tuple.getT2()))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(503).body(empty(id))));
    }

    private SeatMapDto empty(long id) {
        SeatMapDto dto = new SeatMapDto();
        dto.eventoId = id;
        dto.asientos = java.util.List.of();
        return dto;
    }
}