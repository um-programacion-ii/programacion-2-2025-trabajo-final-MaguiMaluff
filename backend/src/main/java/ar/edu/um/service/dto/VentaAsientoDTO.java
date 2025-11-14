package ar.edu.um.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.domain.VentaAsiento} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VentaAsientoDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer fila;

    @NotNull
    @Min(value = 1)
    private Integer columna;

    @NotNull
    @Size(max = 255)
    private String persona;

    @NotNull
    @Size(max = 30)
    private String estado;

    @NotNull
    private VentaDTO venta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public VentaDTO getVenta() {
        return venta;
    }

    public void setVenta(VentaDTO venta) {
        this.venta = venta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VentaAsientoDTO)) {
            return false;
        }

        VentaAsientoDTO ventaAsientoDTO = (VentaAsientoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ventaAsientoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VentaAsientoDTO{" +
            "id=" + getId() +
            ", fila=" + getFila() +
            ", columna=" + getColumna() +
            ", persona='" + getPersona() + "'" +
            ", estado='" + getEstado() + "'" +
            ", venta=" + getVenta() +
            "}";
    }
}
