package ar.edu.um.proxy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO de request para bloquear asientos (se usa en controller).
 */
public class BloquearAsientosRequestDto {

    @NotNull
    private Long eventoId;

    @NotNull
    @Size(min = 1, max = 4, message = "Debe seleccionar entre 1 y 4 asientos")
    private List<AsientoPosicionDto> asientos;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public List<AsientoPosicionDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoPosicionDto> asientos) { this.asientos = asientos; }

    public static class AsientoPosicionDto {
        @NotNull
        private Integer fila;
        @NotNull
        private Integer columna;

        public AsientoPosicionDto() {}
        public AsientoPosicionDto(Integer fila, Integer columna) { this.fila = fila; this.columna = columna; }
        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }
        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }
    }
}