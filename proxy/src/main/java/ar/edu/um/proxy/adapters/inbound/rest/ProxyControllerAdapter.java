package ar.edu.um.proxy.adapters.inbound.rest;

import ar.edu.um.proxy.application.usecase.BlockSeatsUseCase;
import ar.edu.um.proxy.application.usecase.SellSeatsUseCase;
import ar.edu.um.proxy.dto.BloquearAsientosRequestDto;
import ar.edu.um.proxy.dto.VentaRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Adaptador inbound REST: expone endpoints /proxy/... y llama a los UseCases.
 * Nota: recibimos DTO validados y además serializamos el rawPayload que reenviamos al upstream.
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

    @PostMapping(value = "/bloquear", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> bloquear(@Valid @RequestBody BloquearAsientosRequestDto request) throws Exception {
        String raw = mapper.writeValueAsString(request); // serializamos el DTO para reenviar al upstream
        return blockSeatsUseCase.execute(request, raw);
    }

    @PostMapping(value = "/bloquear/raw", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> bloquearRaw(@RequestBody String rawPayload) throws Exception {
        return blockSeatsUseCase.execute(null, rawPayload);
    }

    @PostMapping(value = "/venta", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> venta(@Valid @RequestBody VentaRequestDto request) throws Exception {
        String raw = mapper.writeValueAsString(request);
        return sellSeatsUseCase.execute(request, raw);
    }
}