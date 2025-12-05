package ar.edu.um.proxy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/*
 * DTO para solicitar el bloqueo de asientos.
 * Contiene validaciones con Jakarta Bean Validation (@NotNull, @Size).
 *
 * Notas importantes:
 * - La validación @Size(min=1, max=4) sobre `asientos` asegura que no se solicite bloquear
 *   0 asientos ni más de 4 (regla de negocio).
 * - Las clases internas modelan la posición del asiento (fila, columna).
 * - Este DTO se usa en el controller con @Valid para que Spring lance MethodArgumentNotValidException
 *   en caso de violación de constraints, que luego maneja RestExceptionHandler.
 */
public class BloquearAsientosRequestDto {

    @NotNull
    private Long eventoId; // id del evento a bloquear (no puede ser null)

    @NotNull
    @Size(min = 1, max = 4, message = "Debe seleccionar entre 1 y 4 asientos")
    private List<AsientoPosicionDto> asientos; // lista de posiciones a bloquear

    // Getters / Setters tradicionales (Bean-style) para serialización/deserialización
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public List<AsientoPosicionDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoPosicionDto> asientos) { this.asientos = asientos; }

    /*
     * Clase interna que representa la posición de un asiento (fila/columna).
     * - Ambas propiedades son @NotNull: la posición completa es obligatoria.
     * - Tiene constructor vacío requerido por frameworks de deserialización (Jackson).
     */
    public static class AsientoPosicionDto {
        @NotNull
        private Integer fila;
        @NotNull
        private Integer columna;

        public AsientoPosicionDto() {}
        public AsientoPosicionDto(Integer fila, Integer columna) {
            this.fila = fila;
            this.columna = columna;
        }
        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }
        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }
    }
}