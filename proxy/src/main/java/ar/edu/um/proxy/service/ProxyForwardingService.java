package ar.edu.um.proxy.service;

import ar.edu.um.proxy.config.ProxyProperties;
import ar.edu.um.proxy.exception.ProxyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class ProxyForwardingService {

    private static final Logger log = LoggerFactory.getLogger(ProxyForwardingService.class);

    private final RestClient backendClient;

    // Ajustar endpoints reales del backend
    private static final String BLOQUEAR_ENDPOINT = "/api/proxy/bloquear";
    private static final String VENTA_ENDPOINT = "/api/proxy/venta";

    public ProxyForwardingService(ProxyProperties properties) {
        String baseUrl = properties.getBackend().getBaseUrl();
        this.backendClient = RestClient.builder().baseUrl(baseUrl).build();
        log.info("ProxyForwardingService apuntando a {}", baseUrl);
    }

    public ResponseEntity<String> forwardBloquear(String payloadJson) {
        try {
            return backendClient.post()
                    .uri(BLOQUEAR_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payloadJson)
                    .retrieve()
                    .toEntity(String.class);
        } catch (WebClientResponseException httpEx) {
            // No lo envolvemos: dejamos que el RestExceptionHandler lo procese y propague su status original
            throw httpEx;
        } catch (Exception e) {
            // Errores no-HTTP (conexión, timeouts, etc) -> 400 por defecto via ProxyException
            throw new ProxyException("Error técnico reenviando bloqueo: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<String> forwardVenta(String payloadJson) {
        try {
            return backendClient.post()
                    .uri(VENTA_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payloadJson)
                    .retrieve()
                    .toEntity(String.class);
        } catch (WebClientResponseException httpEx) {
            throw httpEx;
        } catch (Exception e) {
            throw new ProxyException("Error técnico reenviando venta: " + e.getMessage(), e);
        }
    }
}