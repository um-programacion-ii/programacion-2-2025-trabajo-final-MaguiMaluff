/*
 * RedisConfig
 *
 * Define la configuración de Redis para la aplicación:
 * - Crea una conexión a un servidor Redis en modo standalone usando Lettuce.
 * - Expone un RedisTemplate<String, String> para operar con claves y valores de texto.
 * - Configura serializadores de clave y valor como StringRedisSerializer para trabajar con JSON plano.
 *
 * Objetivo:
 * - Permitir leer y escribir datos en Redis utilizando strings (por ejemplo, JSON) de forma compatible.
 */

package ar.edu.um.proxy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Autowired
    private ProxyProperties proxyProperties; // Host y puerto de Redis

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // Conexión standalone a Redis (un solo nodo)
        RedisStandaloneConfiguration cfg = new RedisStandaloneConfiguration();
        cfg.setHostName(proxyProperties.getRedis().getHost());
        cfg.setPort(proxyProperties.getRedis().getPort());
        return new LettuceConnectionFactory(cfg);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory connectionFactory) {
        // Template para operar con claves y valores de tipo String
        RedisTemplate<String, String> t = new RedisTemplate<>();
        t.setConnectionFactory(connectionFactory);

        // Serialización de clave y valor como texto (UTF-8)
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        t.setKeySerializer(stringSerializer);
        t.setValueSerializer(stringSerializer);
        t.setHashKeySerializer(stringSerializer);
        t.setHashValueSerializer(stringSerializer);

        t.afterPropertiesSet();
        return t;
    }
}