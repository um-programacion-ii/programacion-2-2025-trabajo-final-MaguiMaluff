package ar.edu.um.proxy.config;

import ar.edu.um.proxy.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientRequest;

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
    public WebClient catedraWebClient(TokenService tokenService) {
        return WebClient.builder()
                .baseUrl(props.getCatedra().getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .filter((request, next) -> {
                    String path = request.url().getPath();
                    if ("/api/authenticate".equals(path)) {
                        return next.exchange(request);
                    }
                    String token = tokenService.current();
                    if (token != null && !token.isBlank()) {
                        ClientRequest rq = ClientRequest.from(request)
                                .headers(h -> h.setBearerAuth(token))
                                .build();
                        return next.exchange(rq);
                    }
                    return next.exchange(request);
                })
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