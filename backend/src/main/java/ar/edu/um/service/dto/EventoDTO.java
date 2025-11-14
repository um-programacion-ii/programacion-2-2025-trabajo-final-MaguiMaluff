package ar.edu.um.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.domain.Evento} entity.
 */
@Schema(
    description = "JDL para Issue 4: Eventos y Ventas\nIncluye:\n- Entidades: Evento, EventoIntegrante, Venta, VentaAsiento\n- Relaciones\n- DTOs (mapstruct)\n- Servicios (serviceClass)\n- Paginación para Evento y Venta"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String titulo;

    @Size(max = 500)
    private String resumen;

    @Lob
    private String descripcion;

    @NotNull
    private Instant fecha;

    @Size(max = 255)
    private String direccion;

    @Size(max = 1024)
    private String imagen;

    @NotNull
    @Min(value = 1)
    private Integer filaAsientos;

    @NotNull
    @Min(value = 1)
    private Integer columnAsientos;

    @NotNull
    private BigDecimal precioEntrada;

    @Size(max = 120)
    private String eventoTipoNombre;

    @Size(max = 255)
    private String eventoTipoDescripcion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFilaAsientos() {
        return filaAsientos;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Integer getColumnAsientos() {
        return columnAsientos;
    }

    public void setColumnAsientos(Integer columnAsientos) {
        this.columnAsientos = columnAsientos;
    }

    public BigDecimal getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public String getEventoTipoNombre() {
        return eventoTipoNombre;
    }

    public void setEventoTipoNombre(String eventoTipoNombre) {
        this.eventoTipoNombre = eventoTipoNombre;
    }

    public String getEventoTipoDescripcion() {
        return eventoTipoDescripcion;
    }

    public void setEventoTipoDescripcion(String eventoTipoDescripcion) {
        this.eventoTipoDescripcion = eventoTipoDescripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoDTO)) {
            return false;
        }

        EventoDTO eventoDTO = (EventoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoDTO{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", fecha='" + getFecha() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", imagen='" + getImagen() + "'" +
            ", filaAsientos=" + getFilaAsientos() +
            ", columnAsientos=" + getColumnAsientos() +
            ", precioEntrada=" + getPrecioEntrada() +
            ", eventoTipoNombre='" + getEventoTipoNombre() + "'" +
            ", eventoTipoDescripcion='" + getEventoTipoDescripcion() + "'" +
            "}";
    }
}
