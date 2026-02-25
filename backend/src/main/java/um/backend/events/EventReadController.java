package um.backend.events;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/api/internal/events")
public class EventReadController {

    private final EventRepository repo;

    public EventReadController(EventRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Mono<ResponseEntity<List<EventEntity>>> list() {
        return Mono.fromCallable(() -> repo.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EventEntity>> get(@PathVariable long id) {
        return Mono.fromCallable(() -> repo.findById(id).orElse(null))
                .subscribeOn(Schedulers.boundedElastic())
                .map(e -> e != null ? ResponseEntity.ok(e) : ResponseEntity.notFound().build());
    }
}