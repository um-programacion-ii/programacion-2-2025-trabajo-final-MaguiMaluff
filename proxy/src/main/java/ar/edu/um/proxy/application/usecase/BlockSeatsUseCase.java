package ar.edu.um.proxy.application.usecase;

import ar.edu.um.proxy.dto.BloquearAsientosRequestDto;
import ar.edu.um.proxy.ports.outbound.CatedraPort;
import ar.edu.um.proxy.ports.outbound.RedisSeatsPort;
import ar.edu.um.proxy.ports.outbound.TokenPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Caso de uso reactivo que orquesta el bloqueo de asientos.
 */
@Service
public class BlockSeatsUseCase {

    private final Logger log = LoggerFactory.getLogger(BlockSeatsUseCase.class);
    private final CatedraPort catedra;
    private final RedisSeatsPort redis;
    private final TokenPort tokenPort;

    public BlockSeatsUseCase(CatedraPort catedra, RedisSeatsPort redis, TokenPort tokenPort) {
        this.catedra = catedra;
        this.redis = redis;
        this.tokenPort = tokenPort;
    }

    public Mono<ResponseEntity<String>> execute(BloquearAsientosRequestDto request, String rawPayload) {
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Request requerido para bloqueo"));
        }
        if (request.getAsientos() == null || request.getAsientos().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Debe informar entre 1 y 4 asientos"));
        }
        request.getAsientos().forEach(a -> {
            if (a.getFila() == null || a.getFila() <= 0) throw new IllegalArgumentException("Fila inválida");
            if (a.getColumna() == null || a.getColumna() <= 0) throw new IllegalArgumentException("Columna inválida");
        });

        Mono<Void> preRead = Mono.fromCallable(() -> redis.readAsientosRaw(request.getEventoId()))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(redisVal -> log.debug("Estado redis previo: {}", redisVal))
                .doOnError(e -> log.warn("No se pudo leer Redis antes de bloqueo: {}", e.getMessage()))
                .onErrorResume(e -> Mono.empty())
                .then();

        String bearer = tokenPort.current();

        return preRead.then(catedra.bloquearAsientos(rawPayload, bearer));
    }
}