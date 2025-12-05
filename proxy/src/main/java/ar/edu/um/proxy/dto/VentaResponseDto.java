package ar.edu.um.proxy.dto;

import java.util.List;

/*
 * DTO que representa la respuesta del upstream tras una venta.
 * - Contiene info de la venta (ventaId, fechaVenta), lista de asientos con estado,
 *   y flags/descripciones para el resultado.
 *
 * Reutilizar este DTO: si el upstream ya devuelve exactamente este formato,
 * el proxy puede pasarlo "tal cual" al cliente. Si el upstream varía, hay que mapear.
 */
public class VentaResponseDto {

    private Long eventoId;
    private Long ventaId;
    private String fechaVenta;
    private List<AsientoVentaEstadoDto> asientos;
    private boolean resultado;
    private String descripcion;
    private Double precioVenta;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }
    public String getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(String fechaVenta) { this.fechaVenta = fechaVenta; }
    public List<AsientoVentaEstadoDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoVentaEstadoDto> asientos) { this.asientos = asientos; }
    public boolean isResultado() { return resultado; }
    public void setResultado(boolean resultado) { this.resultado = resultado; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }

    public static class AsientoVentaEstadoDto {
        private Integer fila;
        private Integer columna;
        private String persona;
        private String estado;

        public AsientoVentaEstadoDto() {}
        public AsientoVentaEstadoDto(Integer fila, Integer columna, String persona, String estado) {
            this.fila = fila;
            this.columna = columna;
            this.persona = persona;
            this.estado = estado;
        }
        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }
        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }
        public String getPersona() { return persona; }
        public void setPersona(String persona) { this.persona = persona; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}