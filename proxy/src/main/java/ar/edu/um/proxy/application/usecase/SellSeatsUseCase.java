package ar.edu.um.proxy.application.usecase;

import ar.edu.um.proxy.dto.VentaRequestDto;
import ar.edu.um.proxy.ports.outbound.CatedraPort;
import ar.edu.um.proxy.ports.outbound.TokenPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para realizar la venta de asientos.
 * Valida, reenvía a la cátedra y devuelve la respuesta upstream.
 */
@Service
public class SellSeatsUseCase {

    private final Logger log = LoggerFactory.getLogger(SellSeatsUseCase.class);
    private final CatedraPort catedra;
    private final TokenPort tokenPort;

    public SellSeatsUseCase(CatedraPort catedra, TokenPort tokenPort) {
        this.catedra = catedra;
        this.tokenPort = tokenPort;
    }

    public ResponseEntity<String> execute(VentaRequestDto request, String rawPayload) throws Exception {
        if (request.getAsientos() == null || request.getAsientos().isEmpty()) {
            throw new IllegalArgumentException("Debe vender entre 1 y 4 asientos");
        }
        String token = tokenPort.current();
        ResponseEntity<String> resp = catedra.realizarVenta(rawPayload, token);
        return resp;
    }
}