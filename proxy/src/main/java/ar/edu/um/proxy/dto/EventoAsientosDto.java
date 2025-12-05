package ar.edu.um.proxy.dto;

import java.util.List;

/**
 * Representa la estructura almacenada en Redis:
 * {"eventoId":1,"asientos":[{"fila":1,"columna":3,"estado":"Bloqueado","expira":"2025-11-20T02:30:32.980225020Z"}, ... ]}
 *
 * - Esta clase modela el JSON que se guarda/lee desde Redis.
 * - AsientoRedisDto contiene además el campo `expira` (string ISO) usado para bloqueos temporales.
 *
 * Consideración: si se quiere manipular fechas como objetos, conviene mapear `expira` a Instant/OffsetDateTime
 * y configurar ObjectMapper para parseo automático. Actualmente es String, lo que evita problemas de formato
 * pero exige parsing manual si necesitás operar con la fecha.
 */
public class EventoAsientosDto {

    private Long eventoId;
    private List<AsientoRedisDto> asientos;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public List<AsientoRedisDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoRedisDto> asientos) { this.asientos = asientos; }

    public static class AsientoRedisDto {
        private Integer fila;
        private Integer columna;
        private String estado; // Bloqueado / Vendido
        private String expira; // presente sólo en bloqueos (ISO-8601 string)

        public AsientoRedisDto() {}
        public AsientoRedisDto(Integer fila, Integer columna, String estado, String expira) {
            this.fila = fila;
            this.columna = columna;
            this.estado = estado;
            this.expira = expira;
        }
        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }
        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getExpira() { return expira; }
        public void setExpira(String expira) { this.expira = expira; }
    }
}