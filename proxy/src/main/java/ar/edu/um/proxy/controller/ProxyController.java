package ar.edu.um.proxy.controller;

import ar.edu.um.proxy.dto.BloquearAsientosRequestDto;
import ar.edu.um.proxy.dto.VentaRequestDto;
import ar.edu.um.proxy.exception.ProxyNotFoundException;
import ar.edu.um.proxy.service.ProxyForwardingService;
import ar.edu.um.proxy.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proxy")
@Validated
public class ProxyController {

    private final Logger log = LoggerFactory.getLogger(ProxyController.class);
    private final RedisService redisService;
    private final ProxyForwardingService forwardingService;
    private final ObjectMapper mapper = new ObjectMapper();

    // Si rawForward = true se reenvía payload tal cual sin serializar DTO (útil para pruebas)
    private final boolean rawForward = true;

    public ProxyController(RedisService redisService,
                           ProxyForwardingService forwardingService) {
        this.redisService = redisService;
        this.forwardingService = forwardingService;
    }

    @GetMapping(value = "/evento/{id}/asientos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAsientos(@PathVariable("id") Long id) {
        log.debug("GET /proxy/evento/{}/asientos", id);
        String raw = redisService.getAsientosRaw(id);
        if (raw == null) {
            throw new ProxyNotFoundException("No se encontraron asientos para eventoId=" + id);
        }
        return ResponseEntity.ok(raw);
    }

    @PostMapping(value = "/bloquear", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> bloquear(@Valid @RequestBody BloquearAsientosRequestDto request,
                                           @RequestHeader(value = "X-RAW-FORWARD", required = false) String rawToggle) throws Exception {
        String payload = (rawForward && rawToggle == null)
                ? mapper.writeValueAsString(request)
                : mapper.writeValueAsString(request);
        ResponseEntity<String> resp = forwardingService.forwardBloquear(payload);
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

    @PostMapping(value = "/venta", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> venta(@Valid @RequestBody VentaRequestDto request) throws Exception {
        String payload = mapper.writeValueAsString(request);
        ResponseEntity<String> resp = forwardingService.forwardVenta(payload);
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }
}