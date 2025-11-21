package ar.edu.um.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final Logger log = LoggerFactory.getLogger(RedisService.class);
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getAsientosRaw(Long eventoId) {
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
}