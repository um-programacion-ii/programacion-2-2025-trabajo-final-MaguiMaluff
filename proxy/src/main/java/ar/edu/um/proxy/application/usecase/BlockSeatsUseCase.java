package ar.edu.um.proxy.application.usecase;

import ar.edu.um.proxy.dto.BloquearAsientosRequestDto;
import ar.edu.um.proxy.ports.outbound.CatedraPort;
import ar.edu.um.proxy.ports.outbound.RedisSeatsPort;
import ar.edu.um.proxy.ports.outbound.TokenPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Caso de uso que orquesta el bloqueo de asientos:
 * - Valida reglas de negocio básicas (ej. no fila/columna negativa).
 * - Consulta Redis para estado actual (si es necesario).
 * - Reenvía la petición a la cátedra vía CatedraPort aportando el token desde TokenPort.
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

    public ResponseEntity<String> execute(BloquearAsientosRequestDto request, String rawPayload) throws Exception {
        // Validaciones de negocio simples:
        if (request.getAsientos() == null || request.getAsientos().isEmpty()) {
            throw new IllegalArgumentException("Debe informar entre 1 y 4 asientos");
        }
        request.getAsientos().forEach(a -> {
            if (a.getFila() == null || a.getFila() <= 0) throw new IllegalArgumentException("Fila inválida");
            if (a.getColumna() == null || a.getColumna() <= 0) throw new IllegalArgumentException("Columna inválida");
        });

        try {
            String redisVal = redis.readAsientosRaw(request.getEventoId());
            log.debug("Estado redis previo: {}", redisVal);
        } catch (Exception e) {
            log.warn("No se pudo leer Redis antes de bloqueo: {}", e.getMessage());
        }

        // Reenvío al upstream (Cátedra) usando token del TokenPort
        String bearer = tokenPort.current();
        ResponseEntity<String> resp = catedra.bloquearAsientos(rawPayload, bearer);

        return resp;
    }
}