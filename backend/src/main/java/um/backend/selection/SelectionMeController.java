package um.backend.selection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import um.backend.selection.dto.SelectionDtos;

@RestController
@RequestMapping("/api")
public class SelectionMeController {

    private final SelectionService service;

    public SelectionMeController(SelectionService service) {
        this.service = service;
    }

    // Crear/recuperar selección usando el usuario autenticado
    @PostMapping("/selection/me/{eventoId}")
    public Mono<ResponseEntity<SelectionDtos.SelectionResponse>> createOrGetMe(@AuthenticationPrincipal String userId,
                                                                               @PathVariable long eventoId) {
        return Mono.fromCallable(() -> service.getOrCreate(userId, eventoId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(s -> {
                    SelectionDtos.SelectionResponse r = new SelectionDtos.SelectionResponse();
                    r.id = s.getId();
                    r.userId = s.getUserId();
                    r.eventoId = s.getEventoId();
                    r.seats = s.getSeats();
                    r.names = s.getNames();
                    r.stage = s.getStage();
                    r.bloqueadoHasta = s.getBloqueadoHasta();
                    r.updatedAt = s.getUpdatedAt();
                    return ResponseEntity.ok(r);
                });
    }
}