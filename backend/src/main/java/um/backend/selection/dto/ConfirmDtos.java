package um.backend.selection.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ConfirmDtos {

    public static class ConfirmRequest {
        public UUID selectionId;
    }

    public static class ConfirmResponse {
        public UUID saleId;
        public String externalSaleId;
        public long eventoId;
        public String userId;
        public BigDecimal total;
        public Instant createdAt;
        public List<Item> items;

        public static class Item {
            public int fila;
            public int columna;
            public String nombre;
        }
    }
}