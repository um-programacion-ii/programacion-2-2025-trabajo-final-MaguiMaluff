package um.backend.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import um.backend.proxy.ProxyClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/events")
public class EventsController {
    private final ProxyClient proxy;
    private final ObjectMapper mapper;

    public EventsController(ProxyClient proxy, ObjectMapper mapper) {
        this.proxy = proxy;
        this.mapper = mapper;
    }

    @GetMapping
    public Mono<ResponseEntity<EventDto[]>> list() {
        return proxy.eventos()
                .map(body -> {
                    try {
                        return mapper.readValue(body, EventDto[].class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(503)
                        .body(new EventDto[0])));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EventDto>> get(@PathVariable long id) {
        return proxy.evento(id)
                .map(body -> {
                    try {
                        return mapper.readValue(body, EventDto.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(503).build()));
    }
}