package ar.edu.um.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Servicio simple en memoria para mantener el token Bearer.
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final AtomicReference<String> bearerRef = new AtomicReference<>();

    public String current() { return bearerRef.get(); }

    public void update(String newToken) {
        String sanitized = newToken == null ? null : newToken.trim();
        bearerRef.set(sanitized);
        log.info("TokenService: token actualizado {}", sanitized == null ? "(vacío)" : "(presente)");
    }
}