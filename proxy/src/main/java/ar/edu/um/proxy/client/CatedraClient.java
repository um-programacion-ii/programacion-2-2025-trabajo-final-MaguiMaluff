/*
 * CatedraClient
 *
 * Cliente HTTP para consumir lecturas del servicio de la cátedra.
 * - Centraliza las operaciones GET: eventos resumidos, eventos completos, evento por ID, ventas y venta por ID.
 * - Utiliza WebClient con baseUrl y headers configurados en el contexto de Spring.
 * - Devuelve respuestas como JSON en String y aplica timeout para evitar bloqueos prolongados.
 *
 * Objetivo:
 * - Proveer un punto único para obtener datos del servicio de la cátedra de forma consistente.
 */

package ar.edu.um.proxy.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
public class CatedraClient {

    private final Logger log = LoggerFactory.getLogger(CatedraClient.class);
    private final WebClient webClient;

    public CatedraClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public String getEventosResumidos() {
        try {
            return webClient.get()
                    .uri("/api/endpoints/v1/eventos-resumidos")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.warn("Error obteniendo eventos resumidos: {}", e.getMessage(), e);
            return null;
        }
    }

    public String getEventos() {
        try {
            return webClient.get()
                    .uri("/api/endpoints/v1/eventos")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.warn("Error obteniendo eventos completos: {}", e.getMessage(), e);
            return null;
        }
    }

    public String getEvento(Long id) {
        String path = "/api/endpoints/v1/evento/" + id;
        try {
            return webClient.get()
                    .uri(path)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.warn("Error obteniendo evento {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    public String getVentas() {
        try {
            return webClient.get()
                    .uri("/api/endpoints/v1/listar-ventas")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.warn("Error obteniendo ventas: {}", e.getMessage(), e);
            return null;
        }
    }

    public String getVenta(Long id) {
        String path = "/api/endpoints/v1/listar-venta/" + id;
        try {
            return webClient.get()
                    .uri(path)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.warn("Error obteniendo venta {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    public String forzarActualizacion() {
        try {
            return webClient.get()
                    .uri("/api/endpoints/v1/forzar-actualizacion")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.warn("Error forzando actualización: {}", e.getMessage(), e);
            return null;
        }
    }
}