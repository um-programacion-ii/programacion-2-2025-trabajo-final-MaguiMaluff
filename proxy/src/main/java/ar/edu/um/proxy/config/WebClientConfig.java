package ar.edu.um.proxy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

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
            builder.defaultHeaders(headers -> headers.setBearerAuth(token));
        }
        return builder.build();
    }
}