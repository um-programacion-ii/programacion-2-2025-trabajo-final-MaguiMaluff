package ar.edu.um.proxy.adapters.outbound.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RedisSeatRepository {

    private final RedisTemplate<String, String> redis;
    private final String seatKeyPattern;

    public RedisSeatRepository(
            @Qualifier("redisTemplate") RedisTemplate<String, String> redis,
            @Value("${proxy.redis.seat-key-pattern:evento_%d}") String seatKeyPattern
    ) {
        this.redis = redis;
        this.seatKeyPattern = seatKeyPattern;
    }

    public Optional<String> findSeatMapRaw(long eventoId) {
        String key = key(eventoId);
        String value = redis.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    public boolean setIfAbsent(long eventoId, String json) {
        String key = key(eventoId);
        Boolean ok = redis.opsForValue().setIfAbsent(key, json);
        return Boolean.TRUE.equals(ok);
    }

    private String key(long eventoId) {
        return String.format(seatKeyPattern, eventoId); // p.ej. "evento_1"
    }
}