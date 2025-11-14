package ar.edu.um.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Venta.
 */
@Entity
@Table(name = "venta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fecha_venta", nullable = false)
    private Instant fechaVenta;

    @NotNull
    @Column(name = "precio_venta", precision = 21, scale = 2, nullable = false)
    private BigDecimal precioVenta;

    @NotNull
    @Column(name = "resultado", nullable = false)
    private Boolean resultado;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotNull
    @Min(value = 0)
    @Max(value = 4)
    @Column(name = "cantidad_asientos", nullable = false)
    private Integer cantidadAsientos;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "integrantes" }, allowSetters = true)
    private Evento evento;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Venta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFechaVenta() {
        return this.fechaVenta;
    }

    public Venta fechaVenta(Instant fechaVenta) {
        this.setFechaVenta(fechaVenta);
        return this;
    }

    public void setFechaVenta(Instant fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getPrecioVenta() {
        return this.precioVenta;
    }

    public Venta precioVenta(BigDecimal precioVenta) {
        this.setPrecioVenta(precioVenta);
        return this;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Boolean getResultado() {
        return this.resultado;
    }

    public Venta resultado(Boolean resultado) {
        this.setResultado(resultado);
        return this;
    }

    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Venta descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidadAsientos() {
        return this.cantidadAsientos;
    }

    public Venta cantidadAsientos(Integer cantidadAsientos) {
        this.setCantidadAsientos(cantidadAsientos);
        return this;
    }

    public void setCantidadAsientos(Integer cantidadAsientos) {
        this.cantidadAsientos = cantidadAsientos;
    }

    public Evento getEvento() {
        return this.evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Venta evento(Evento evento) {
        this.setEvento(evento);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venta)) {
            return false;
        }
        return getId() != null && getId().equals(((Venta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Venta{" +
            "id=" + getId() +
            ", fechaVenta='" + getFechaVenta() + "'" +
            ", precioVenta=" + getPrecioVenta() +
            ", resultado='" + getResultado() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", cantidadAsientos=" + getCantidadAsientos() +
            "}";
    }
}
