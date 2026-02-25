package um.backend.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/proxy")
public class ProxyWebhookController {

    private static final Logger log = LoggerFactory.getLogger(ProxyWebhookController.class);

    private final EventSyncService syncService;
    private final String secret;

    public ProxyWebhookController(EventSyncService syncService,
                                  @Value("${backend.webhook.secret:}") String secret) {
        this.syncService = syncService;
        this.secret = secret == null ? "" : secret;
    }

    @PostMapping(value = "/event-change", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String, Object>>> eventChange(
            @RequestBody(required = false) Map<String, Object> body) {

        Long eventId;
        if (body != null && body.get("eventId") instanceof Number num) {
            eventId = num.longValue();
        } else {
            eventId = null;
        }

        if (eventId != null) {
            return syncService.syncOne(eventId)
                    .map(ok -> ResponseEntity.ok(Map.<String,Object>of("status", "ok", "synced", 1, "eventId", eventId)))
                    .onErrorResume(e -> {
                        log.warn("syncOne({}) failed: {}", eventId, e.getMessage(), e);
                        return Mono.just(ResponseEntity.status(502).body(Map.<String,Object>of("status", "error", "message", e.getMessage())));
                    });
        } else {
            return syncService.syncAllFromProxy()
                    .map(cnt -> ResponseEntity.ok(Map.<String,Object>of("status", "ok", "synced", cnt)))
                    .onErrorResume(e -> {
                        log.warn("syncAll failed: {}", e.getMessage(), e);
                        return Mono.just(ResponseEntity.status(502).body(Map.<String,Object>of("status", "error", "message", e.getMessage())));
                    });
        }
    }
}