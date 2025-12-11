package ar.edu.um.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Beans WebClient para cátedra y backend.
 * Se nombran para inyección explícita en adaptadores.
 */
@Configuration
public class WebClientConfig {

    private final ProxyProperties props;

    public WebClientConfig(ProxyProperties props) {
        this.props = props;
    }

    @Bean("catedraWebClient")
    public WebClient catedraWebClient() {
        return WebClient.builder()
                .baseUrl(props.getCatedra().getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    @Bean("backendWebClient")
    public WebClient backendWebClient() {
        return WebClient.builder()
                .baseUrl(props.getBackend().getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}