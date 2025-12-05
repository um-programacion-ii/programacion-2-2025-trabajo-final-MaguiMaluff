package ar.edu.um.proxy.dto;

import java.util.List;

/*
 * DTO de respuesta para operaciones de bloqueo.
 * - resultado: indica si la operación tuvo éxito.
 * - descripcion: texto con motivo/descripcion del resultado.
 * - eventoId: id del evento afectado (útil para correlación).
 * - asientos: lista de asientos con su estado resultante.
 */
public class BloquearAsientosResponseDto {

    private boolean resultado;
    private String descripcion;
    private Long eventoId;
    private List<AsientoEstadoDto> asientos;

    public boolean isResultado() { return resultado; }
    public void setResultado(boolean resultado) { this.resultado = resultado; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public List<AsientoEstadoDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoEstadoDto> asientos) { this.asientos = asientos; }

    /*
     * AsientoEstadoDto: estado resultante de cada posición solicitada
     * - estado: p. ej. "Bloqueado" / "Vendido" / "NoDisponible"
     * - fila / columna: coordenadas del asiento
     */
    public static class AsientoEstadoDto {
        private String estado;
        private Integer fila;
        private Integer columna;

        public AsientoEstadoDto() {}
        public AsientoEstadoDto(String estado, Integer fila, Integer columna) {
            this.estado = estado;
            this.fila = fila;
            this.columna = columna;
        }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }
        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }
    }
}