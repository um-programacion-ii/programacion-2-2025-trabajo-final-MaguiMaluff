package ar.edu.um.proxy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Autowired
    private ProxyProperties proxyProperties;

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
        return t;
    }
}