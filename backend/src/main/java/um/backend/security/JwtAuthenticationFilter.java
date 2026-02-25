package um.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/signup",
            "/api/auth/login",
            "/api/proxy/event-change"
    );

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwt;
    private final SessionService sessionService;

    public JwtAuthenticationFilter(JwtService jwt, SessionService sessionService) {
        super((ReactiveAuthenticationManager) authentication -> Mono.just(authentication)); // no-op
        this.jwt = jwt;
        this.sessionService = sessionService;

        setRequiresAuthenticationMatcher(this::requiresAuth);
        setAuthenticationFailureHandler((webFilterExchange, ex) -> {
            log.debug("Auth fallida: {}", ex.getMessage());
            return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
        });

        setServerAuthenticationConverter(this::convert);
    }

    private Mono<ServerWebExchangeMatcher.MatchResult> requiresAuth(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        log.trace("JwtAuthFilter path={}", path);

        if (isPublic(path)) {
            return ServerWebExchangeMatcher.MatchResult.notMatch();
        }
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        boolean hasBearer = auth != null && auth.startsWith("Bearer ");
        return hasBearer
                ? ServerWebExchangeMatcher.MatchResult.match()
                : ServerWebExchangeMatcher.MatchResult.notMatch();
    }

    private boolean isPublic(String path) {
        if (path == null) return false;
        if (PUBLIC_PATHS.contains(path)) return true;
        return path.startsWith("/api/proxy/event-change");
    }

    private Mono<Authentication> convert(ServerWebExchange exchange) {
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) return Mono.empty();
        String token = auth.substring(7);
        try {
            String userId = jwt.validateAndGetSubject(token);
            boolean valid = sessionService.touchAndValidate(userId);
            if (!valid) return Mono.empty();
            var authToken = new UsernamePasswordAuthenticationToken(
                    userId, token, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            return Mono.just(authToken);
        } catch (Exception e) {
            log.debug("JWT inválido: {}", e.getMessage());
            return Mono.empty();
        }
    }
}