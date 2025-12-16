package ar.edu.um.proxy.application.usecase;

import ar.edu.um.proxy.dto.VentaRequestDto;
import ar.edu.um.proxy.ports.outbound.CatedraPort;
import ar.edu.um.proxy.ports.outbound.TokenPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * Caso de uso reactivo para realizar la venta de asientos.
 * - Autocompleta fecha (now UTC).
 * - Obtiene precio por entrada desde cátedra y calcula total = precioEntrada * cantidadAsientos (redondeado a 2 decimales).
 * - Si falla obtener precio, usa el precioVenta del request como precio por entrada y también lo multiplica por la cantidad.
 */
@Service
public class SellSeatsUseCase {

    private final Logger log = LoggerFactory.getLogger(SellSeatsUseCase.class);
    private final CatedraPort catedra;
    private final TokenPort tokenPort;
    private final ObjectMapper mapper = new ObjectMapper();

    public SellSeatsUseCase(CatedraPort catedra, TokenPort tokenPort) {
        this.catedra = catedra;
        this.tokenPort = tokenPort;
    }

    public Mono<ResponseEntity<String>> execute(VentaRequestDto request, String rawPayload) {
        if (request == null || request.getAsientos() == null || request.getAsientos().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Debe vender entre 1 y 4 asientos"));
        }
        if (request.getEventoId() == null) {
            return Mono.error(new IllegalArgumentException("eventoId es requerido"));
        }
        final int cantidad = request.getAsientos().size();

        Mono<BigDecimal> totalPrecioMono = catedra.evento(request.getEventoId())
                .map(body -> {
                    JsonNode n = null;
                    try {
                        n = mapper.readTree(body);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    if (n.has("precioEntrada") && !n.get("precioEntrada").isNull()) {
                        double precioEntrada = n.get("precioEntrada").asDouble();
                        return BigDecimal.valueOf(precioEntrada)
                                .multiply(BigDecimal.valueOf(cantidad))
                                .setScale(2, RoundingMode.HALF_UP);
                    }
                    throw new IllegalStateException("precioEntrada no presente en el evento " + request.getEventoId());
                })
                .onErrorResume(e -> {
                    Double reqPrecio = request.getPrecioVenta();
                    if (reqPrecio != null) {
                        BigDecimal total = BigDecimal.valueOf(reqPrecio)
                                .multiply(BigDecimal.valueOf(cantidad))
                                .setScale(2, RoundingMode.HALF_UP);
                        log.warn("No se pudo obtener precioEntrada desde cátedra, usando precio del request por entrada x{} = {} ({})",
                                cantidad, total, e.getMessage());
                        return Mono.just(total);
                    }
                    return Mono.error(new IllegalStateException("No se pudo determinar precio total de la venta"));
                });

        return totalPrecioMono.flatMap(total -> {
            ObjectNode payload = mapper.createObjectNode();
            payload.put("eventoId", request.getEventoId());
            payload.put("fecha", Instant.now().toString());
            payload.put("precioVenta", total.doubleValue());

            ArrayNode asientosArr = payload.putArray("asientos");
            request.getAsientos().forEach(a -> {
                ObjectNode seat = mapper.createObjectNode();
                seat.put("fila", a.getFila());
                seat.put("columna", a.getColumna());
                seat.put("persona", a.getPersona());
                asientosArr.add(seat);
            });

            String finalJson = payload.toString();
            String token = tokenPort.current();
            return catedra.realizarVenta(finalJson, token);
        });
    }
}