package ar.edu.um.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/*
 * WebClientConfig
 *
 * - Define un único bean WebClient llamado 'webClient' con baseUrl y headers
 *   tomados de ProxyProperties (proxy.catedra.*).
 * - Si proxy.catedra.token está presente, se añade como Bearer a todas las peticiones.
 */
@Configuration
public class WebClientConfig {

    private final ProxyProperties proxyProperties;

    public WebClientConfig(ProxyProperties proxyProperties) {
        this.proxyProperties = proxyProperties;
    }

    @Bean
    public WebClient webClient() {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(proxyProperties.getCatedra().getBaseUrl())
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                                .build()
                )
                .defaultHeader("Content-Type", "application/json");

        String token = proxyProperties.getCatedra().getToken();
        if (token != null && !token.isBlank()) {
            builder.defaultHeaders(h -> h.setBearerAuth(token));
        }

        return builder.build();
    }
}