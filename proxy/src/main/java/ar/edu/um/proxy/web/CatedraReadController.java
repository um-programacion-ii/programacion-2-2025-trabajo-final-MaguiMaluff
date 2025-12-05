package ar.edu.um.proxy.web;

import ar.edu.um.proxy.client.CatedraClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * CatedraReadController
 *
 * Controlador REST de solo lectura que expone endpoints HTTP para consultar
 * datos del servicio de la cátedra a través del Proxy.
 *
 * - Internamente delega en CatedraClient (que usa WebClient configurado con:
 *   baseUrl = proxy.catedra.base-url y Authorization Bearer si hay token).
 * - Devuelve el JSON crudo (String) que retorna la cátedra.
 * - Si hay error al llamar a la cátedra, responde 502 Bad Gateway con un JSON simple.
 *
 * Este controlador es ideal para probar con Postman sin tocar Kafka.
 * Endpoints:
 *   GET  /catedra/eventos-resumidos
 *   GET  /catedra/eventos
 *   GET  /catedra/evento/{id}
 *   GET  /catedra/ventas
 *   GET  /catedra/venta/{id}
 *   POST /catedra/forzar-actualizacion
 */

@RestController
@RequestMapping("/catedra")
public class CatedraReadController {

    private static final Logger log = LoggerFactory.getLogger(CatedraReadController.class);
    private final CatedraClient client;

    public CatedraReadController(CatedraClient client) {
        this.client = client;
    }

    @GetMapping("/eventos-resumidos")
    public ResponseEntity<String> eventosResumidos() {
        String json = client.getEventosResumidos();
        return buildResponse(json, "eventos-resumidos");
    }

    @GetMapping("/eventos")
    public ResponseEntity<String> eventos() {
        String json = client.getEventos();
        return buildResponse(json, "eventos");
    }

    @GetMapping("/evento/{id}")
    public ResponseEntity<String> evento(@PathVariable Long id) {
        String json = client.getEvento(id);
        return buildResponse(json, "evento " + id);
    }

    @GetMapping("/ventas")
    public ResponseEntity<String> ventas() {
        String json = client.getVentas();
        return buildResponse(json, "ventas");
    }

    @GetMapping("/venta/{id}")
    public ResponseEntity<String> venta(@PathVariable Long id) {
        String json = client.getVenta(id);
        return buildResponse(json, "venta " + id);
    }

    @PostMapping("/forzar-actualizacion")
    public ResponseEntity<String> forzarActualizacion() {
        String json = client.forzarActualizacion();
        return buildResponse(json, "forzar-actualizacion");
    }

    private ResponseEntity<String> buildResponse(String json, String op) {
        if (json == null) {
            log.warn("Cátedra sin respuesta para {}", op);
            return ResponseEntity.status(502).body("{\"error\":\"catedra sin respuesta\"}");
        }
        return ResponseEntity.ok(json);
    }
}
