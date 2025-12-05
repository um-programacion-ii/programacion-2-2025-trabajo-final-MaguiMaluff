package ar.edu.um.proxy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/*
 * WebClientConfig (comentado)
 *
 * - Define bean WebClient configurado con proxy.catedra.base-url (ProxyProperties).
 * - Si proxy.catedra.token existe, lo añade como default header Bearer para todas las peticiones
 *   hechas por este WebClient. Esto evita la duplicidad de headers si los clientes confían en el bean.
 *
 * Notas:
 * - Si prefieres no tener token en defaultHeaders (por ejemplo, porque el token cambia), en su lugar
 *   inyecta ProxyProperties y añade el header por petición.
 * - El maxInMemorySize está aumentado a 16MiB para permitir respuestas de ese tamaño en memoria.
 */
@Configuration
public class WebClientConfig {

    @Autowired
    private ProxyProperties proxyProperties;

    @Bean
    public WebClient catedraWebClient() {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(proxyProperties.getCatedra().getBaseUrl())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build());

        String token = proxyProperties.getCatedra().getToken();
        if (token != null && !token.isBlank()) {
            // Colocamos token como header por defecto (se añade a TODAS las peticiones de este WebClient)
            builder.defaultHeaders(headers -> headers.setBearerAuth(token));
        }
        return builder.build();
    }
}