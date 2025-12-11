package ar.edu.um.proxy.adapters.outbound.catedra;

import ar.edu.um.proxy.ports.outbound.CatedraPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Implementación del puerto CatedraPort usando WebClient.
 *
 * - Implementa ar.edu.um.proxy.ports.outbound.CatedraPort para que Spring pueda inyectarla
 *   donde se requiere (use-cases).
 * - Maneja token opcional recibido desde los use-cases (no consulta TokenService aquí).
 * - Trabaja con payloads JSON crudos (String) para forward al upstream.
 */
@Component
public class CatedraWebClientAdapter implements CatedraPort {

    private static final Logger log = LoggerFactory.getLogger(CatedraWebClientAdapter.class);
    private final WebClient client;

    // Inyectamos el WebClient nombrado en WebClientConfig
    public CatedraWebClientAdapter(@Qualifier("catedraWebClient") WebClient client) {
        this.client = client;
    }

    private String doGet(String path) throws Exception {
        try {
            return client.get()
                    .uri(path)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException wre) {
            log.warn("Upstream GET {} returned status {}: {}", path, wre.getRawStatusCode(), wre.getResponseBodyAsString());
            throw wre;
        } catch (Exception e) {
            log.error("Error realizando GET a cátedra {}: {}", path, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String eventosResumidos() throws Exception {
        return doGet("/api/endpoints/v1/eventos-resumidos");
    }

    @Override
    public String eventos() throws Exception {
        return doGet("/api/endpoints/v1/eventos");
    }

    @Override
    public String evento(long id) throws Exception {
        return doGet("/api/endpoints/v1/evento/" + id);
    }

    @Override
    public String ventas() throws Exception {
        return doGet("/api/endpoints/v1/listar-ventas");
    }

    @Override
    public String venta(long id) throws Exception {
        return doGet("/api/endpoints/v1/listar-venta/" + id);
    }

    @Override
    public ResponseEntity<String> bloquearAsientos(String payloadJson, String bearerToken) throws Exception {
        try {
            if (bearerToken != null && !bearerToken.isBlank()) {
                return client.post()
                        .uri("/api/endpoints/v1/bloquear-asientos")
                        .headers(h -> h.setBearerAuth(bearerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(payloadJson)
                        .retrieve()
                        .toEntity(String.class)
                        .block();
            } else {
                return client.post()
                        .uri("/api/endpoints/v1/bloquear-asientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(payloadJson)
                        .retrieve()
                        .toEntity(String.class)
                        .block();
            }
        } catch (WebClientResponseException wre) {
            log.warn("Bloquear asientos upstream falló: status={}, body={}", wre.getRawStatusCode(), wre.getResponseBodyAsString());
            throw wre;
        } catch (Exception e) {
            log.error("Error reenviando bloqueo a cátedra: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<String> realizarVenta(String payloadJson, String bearerToken) throws Exception {
        try {
            if (bearerToken != null && !bearerToken.isBlank()) {
                return client.post()
                        .uri("/api/endpoints/v1/realizar-venta")
                        .headers(h -> h.setBearerAuth(bearerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(payloadJson)
                        .retrieve()
                        .toEntity(String.class)
                        .block();
            } else {
                return client.post()
                        .uri("/api/endpoints/v1/realizar-venta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(payloadJson)
                        .retrieve()
                        .toEntity(String.class)
                        .block();
            }
        } catch (WebClientResponseException wre) {
            log.warn("Realizar venta upstream falló: status={}, body={}", wre.getRawStatusCode(), wre.getResponseBodyAsString());
            throw wre;
        } catch (Exception e) {
            log.error("Error reenviando venta a cátedra: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String forzarActualizacion() throws Exception {
        return doGet("/api/endpoints/v1/forzar-actualizacion");
    }
}