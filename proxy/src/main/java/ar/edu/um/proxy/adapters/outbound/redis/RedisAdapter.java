package ar.edu.um.proxy.adapters.outbound.redis;

import ar.edu.um.proxy.ports.outbound.RedisSeatsPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementación de RedisSeatsPort usando RedisTemplate<String,String>.
 * Lee/Escribe JSON crudo en la key "evento_{id}".
 */
@Component
public class RedisAdapter implements RedisSeatsPort {

    private final Logger log = LoggerFactory.getLogger(RedisAdapter.class);
    private final RedisTemplate<String, String> redisTemplate;

    public RedisAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String readAsientosRaw(Long eventoId) {
        String key = "evento_" + eventoId;
        try {
            String val = redisTemplate.opsForValue().get(key);
            if (val == null) {
                log.debug("Redis key {} no encontrada", key);
            } else {
                log.debug("Valor recuperado para {} ({} bytes)", key, val.length());
            }
            return val;
        } catch (Exception e) {
            log.error("Error leyendo Redis key {}: {}", key, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void writeAsientosRaw(Long eventoId, String json) {
        String key = "evento_" + eventoId;
        try {
            redisTemplate.opsForValue().set(key, json);
            log.debug("Redis key {} escrita ({} bytes)", key, json == null ? 0 : json.length());
        } catch (Exception e) {
            log.error("Error escribiendo Redis key {}: {}", key, e.getMessage(), e);
            throw e;
        }
    }
}