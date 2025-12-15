package ar.edu.um.proxy.adapters.outbound.token;

import ar.edu.um.proxy.ports.outbound.TokenPort;
import ar.edu.um.proxy.service.TokenService;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa TokenPort delegando al TokenService in-memory.
 * TokenService mantiene token en memoria y es actualizado por CatedraAuthService.
 */
@Component
public class TokenServiceAdapter implements TokenPort {

    private final TokenService tokenService;

    public TokenServiceAdapter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public String current() {
        return tokenService.current();
    }

    @Override
    public void update(String token) {
        tokenService.update(token);
    }
}