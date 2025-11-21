package ar.edu.um.proxy.dto;

import java.util.List;

/**
 * Representa la estructura almacenada en Redis:
 * {"eventoId":1,"asientos":[{"fila":1,"columna":3,"estado":"Bloqueado","expira":"2025-11-20T02:30:32.980225020Z"}, ... ]}
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
        private String expira; // presente sólo en bloqueos

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