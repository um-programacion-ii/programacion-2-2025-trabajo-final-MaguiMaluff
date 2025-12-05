package ar.edu.um.proxy.controller;

import ar.edu.um.proxy.dto.BloquearAsientosRequestDto;
import ar.edu.um.proxy.dto.VentaRequestDto;
import ar.edu.um.proxy.exception.ProxyNotFoundException;
import ar.edu.um.proxy.exception.ProxyException;
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

/*
 * ProxyController (versión comentada y modificada)
 *
 * Cambios principales:
 * - Inyección de ObjectMapper (en lugar de crear new ObjectMapper()) para usar la configuración global de Jackson.
 * - Separación clara del comportamiento "raw forward": ahora existe un endpoint específico /proxy/bloquear/raw
 *   para reenviar JSON tal cual sin validación/serialización.
 * - Validaciones y manejo de respuestas nulas: si forwardingService devuelve null se lanza ProxyException.
 *
 * Diseño:
 * - /proxy/evento/{id}/asientos -> lee datos en crudo desde RedisService (espera JSON String)
 * - /proxy/bloquear -> recibe DTO validado, lo serializa y lo reenvía
 * - /proxy/bloquear/raw -> recibe JSON crudo como String y lo reenvía tal cual
 * - /proxy/venta -> recibe DTO validado, lo serializa y lo reenvía
 *
 * Notas:
 * - Si prefieres toggle por header para raw-forward, podemos implementarlo. Aquí se optó por claridad y seguridad.
 */
@RestController
@RequestMapping("/proxy")
@Validated
public class ProxyController {

    private final Logger log = LoggerFactory.getLogger(ProxyController.class);

    private final RedisService redisService;
    private final ProxyForwardingService forwardingService;
    private final ObjectMapper mapper; // Inyectado para usar config global de Jackson

    public ProxyController(RedisService redisService,
                           ProxyForwardingService forwardingService,
                           ObjectMapper mapper) {
        this.redisService = redisService;
        this.forwardingService = forwardingService;
        this.mapper = mapper;
    }

    /**
     * GET /proxy/evento/{id}/asientos
     * Devuelve JSON en crudo recuperado desde Redis. Si no existe, lanza ProxyNotFoundException -> 404.
     */
    @GetMapping(value = "/evento/{id}/asientos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAsientos(@PathVariable("id") Long id) {
        log.debug("GET /proxy/evento/{}/asientos", id);
        String raw = redisService.getAsientosRaw(id);
        if (raw == null) {
            // Este tipo de excepción es manejada por RestExceptionHandler -> NOT_FOUND
            throw new ProxyNotFoundException("No se encontraron asientos para eventoId=" + id);
        }
        return ResponseEntity.ok(raw);
    }

    /**
     * POST /proxy/bloquear
     * Endpoint "normal": recibe DTO con validación (@Valid), serializa a JSON y reenvía.
     * Si el forwarding falla (resp == null o excepción), se lanza ProxyException.
     */
    @PostMapping(value = "/bloquear", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> bloquear(@Valid @RequestBody BloquearAsientosRequestDto request) throws Exception {
        // Serializamos el DTO usando el ObjectMapper inyectado (configuración global)
        String payload = mapper.writeValueAsString(request);

        // Delegamos al servicio que hace el forward
        ResponseEntity<String> resp = forwardingService.forwardBloquear(payload);

        // Validamos la respuesta del forwarding; si hay fallo lanzamos ProxyException
        if (resp == null) {
            throw new ProxyException("Error al reenviar petición de bloqueo");
        }
        // Devolvemos exactamente el status y body que devolvió el upstream
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

    /**
     * POST /proxy/bloquear/raw
     * Endpoint explícito para reenviar el JSON tal cual (sin validación DTO).
     * Útil para pruebas o cuando el frontend ya tiene la estructura exacta que quiere enviar.
     */
    @PostMapping(value = "/bloquear/raw", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> bloquearRaw(@RequestBody String rawPayload) throws Exception {
        ResponseEntity<String> resp = forwardingService.forwardBloquear(rawPayload);
        if (resp == null) {
            throw new ProxyException("Error al reenviar petición de bloqueo (raw)");
        }
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

    /**
     * POST /proxy/venta
     * Similar a /bloquear: recibe DTO validado, serializa y reenvía.
     */
    @PostMapping(value = "/venta", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> venta(@Valid @RequestBody VentaRequestDto request) throws Exception {
        String payload = mapper.writeValueAsString(request);
        ResponseEntity<String> resp = forwardingService.forwardVenta(payload);
        if (resp == null) {
            throw new ProxyException("Error al reenviar petición de venta");
        }
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }
}