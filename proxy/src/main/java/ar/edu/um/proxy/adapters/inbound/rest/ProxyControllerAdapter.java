package ar.edu.um.proxy.adapters.inbound.rest;

import ar.edu.um.proxy.application.usecase.BlockSeatsUseCase;
import ar.edu.um.proxy.application.usecase.SellSeatsUseCase;
import ar.edu.um.proxy.dto.BloquearAsientosRequestDto;
import ar.edu.um.proxy.dto.VentaRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

/**
 * Adaptador inbound REST reactivo: expone endpoints /proxy/... y llama a los UseCases.
 */
@RestController
@RequestMapping("/proxy")
@Validated
public class ProxyControllerAdapter {

    private final Logger log = LoggerFactory.getLogger(ProxyControllerAdapter.class);
    private final BlockSeatsUseCase blockSeatsUseCase;
    private final SellSeatsUseCase sellSeatsUseCase;
    private final ObjectMapper mapper;

    public ProxyControllerAdapter(BlockSeatsUseCase blockSeatsUseCase,
                                  SellSeatsUseCase sellSeatsUseCase,
                                  ObjectMapper mapper) {
        this.blockSeatsUseCase = blockSeatsUseCase;
        this.sellSeatsUseCase = sellSeatsUseCase;
        this.mapper = mapper;
    }

    @PostMapping(value = "/bloquear", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<String>> bloquear(@Valid @RequestBody BloquearAsientosRequestDto request) throws Exception {
        String raw = mapper.writeValueAsString(request);
        return blockSeatsUseCase.execute(request, raw);
    }

    @PostMapping(value = "/bloquear/raw", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<String>> bloquearRaw(@RequestBody String rawPayload) {
        // Para la versión raw no podemos validar DTO; si se requiere, se podría añadir parsing previo.
        // Aquí retornamos error si falta payload.
        if (rawPayload == null || rawPayload.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().body("{\"error\":\"payload vacío\"}"));
        }
        // No tenemos request DTO para validar filas/columnas; dejar que upstream valide.
        // Se puede extender parseando y validando si lo necesitás.
        // Invocamos el usecase con request nulo para evitar doble serialización:
        return blockSeatsUseCase.execute(new BloquearAsientosRequestDto(), rawPayload);
    }

    @PostMapping(value = "/venta", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<String>> venta(@Valid @RequestBody VentaRequestDto request) throws Exception {
        String raw = mapper.writeValueAsString(request);
        return sellSeatsUseCase.execute(request, raw);
    }
}