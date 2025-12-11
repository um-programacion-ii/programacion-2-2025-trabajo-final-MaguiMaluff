package ar.edu.um.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuración Redis (Lettuce + RedisTemplate<String,String>).
 */
@Configuration
public class RedisConfig {

    private final ProxyProperties proxyProperties;

    public RedisConfig(ProxyProperties proxyProperties) {
        this.proxyProperties = proxyProperties;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration cfg = new RedisStandaloneConfiguration();
        cfg.setHostName(proxyProperties.getRedis().getHost());
        cfg.setPort(proxyProperties.getRedis().getPort());
        return new LettuceConnectionFactory(cfg);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, String> t = new RedisTemplate<>();
        t.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        t.setKeySerializer(stringSerializer);
        t.setValueSerializer(stringSerializer);
        t.setHashKeySerializer(stringSerializer);
        t.setHashValueSerializer(stringSerializer);
        t.afterPropertiesSet();
        return t;
    }
}