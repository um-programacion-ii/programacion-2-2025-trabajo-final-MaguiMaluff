package ar.edu.um.proxy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/*
 * DTO de request para realizar una venta.
 * - fecha: se espera una cadena ISO-8601 (el upstream puede parsearla).
 * - precioVenta: precio aplicado a la venta.
 * - asientos: lista de asientos con la persona asignada.
 *
 * Validaciones:
 * - NotNull: campos obligatorios
 * - Size(min=1, max=4): solo permitir ventas de hasta 4 asientos (regla de negocio)
 */
public class VentaRequestDto {

    @NotNull
    private Long eventoId;

    // ISO-8601 string (fechaVenta propuesta en payload, aquí la llamamos fecha)
    @NotNull
    private String fecha;

    @NotNull
    private Double precioVenta;

    @NotNull
    @Size(min = 1, max = 4, message = "Debe vender entre 1 y 4 asientos")
    private List<AsientoVentaDto> asientos;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    public List<AsientoVentaDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoVentaDto> asientos) { this.asientos = asientos; }

    /*
     * AsientoVentaDto: además de fila/columna, incluye la persona asociada a la entrada.
     * - persona puede necesitar validación adicional (p. ej. tamaño, formato).
     */
    public static class AsientoVentaDto {
        @NotNull
        private Integer fila;
        @NotNull
        private Integer columna;
        @NotNull
        private String persona;

        public AsientoVentaDto() {}
        public AsientoVentaDto(Integer fila, Integer columna, String persona) {
            this.fila = fila;
            this.columna = columna;
            this.persona = persona;
        }
        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }
        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }
        public String getPersona() { return persona; }
        public void setPersona(String persona) { this.persona = persona; }
    }
}