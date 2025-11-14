package ar.edu.um.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * JDL para Issue 4: Eventos y Ventas
 * Incluye:
 * - Entidades: Evento, EventoIntegrante, Venta, VentaAsiento
 * - Relaciones
 * - DTOs (mapstruct)
 * - Servicios (serviceClass)
 * - Paginación para Evento y Venta
 */
@Entity
@Table(name = "evento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @Size(max = 500)
    @Column(name = "resumen", length = 500)
    private String resumen;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @Size(max = 255)
    @Column(name = "direccion", length = 255)
    private String direccion;

    @Size(max = 1024)
    @Column(name = "imagen", length = 1024)
    private String imagen;

    @NotNull
    @Min(value = 1)
    @Column(name = "fila_asientos", nullable = false)
    private Integer filaAsientos;

    @NotNull
    @Min(value = 1)
    @Column(name = "column_asientos", nullable = false)
    private Integer columnAsientos;

    @NotNull
    @Column(name = "precio_entrada", precision = 21, scale = 2, nullable = false)
    private BigDecimal precioEntrada;

    @Size(max = 120)
    @Column(name = "evento_tipo_nombre", length = 120)
    private String eventoTipoNombre;

    @Size(max = 255)
    @Column(name = "evento_tipo_descripcion", length = 255)
    private String eventoTipoDescripcion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evento")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "evento" }, allowSetters = true)
    private Set<EventoIntegrante> integrantes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Evento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Evento titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return this.resumen;
    }

    public Evento resumen(String resumen) {
        this.setResumen(resumen);
        return this;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Evento descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return this.fecha;
    }

    public Evento fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public Evento direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return this.imagen;
    }

    public Evento imagen(String imagen) {
        this.setImagen(imagen);
        return this;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFilaAsientos() {
        return this.filaAsientos;
    }

    public Evento filaAsientos(Integer filaAsientos) {
        this.setFilaAsientos(filaAsientos);
        return this;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Integer getColumnAsientos() {
        return this.columnAsientos;
    }

    public Evento columnAsientos(Integer columnAsientos) {
        this.setColumnAsientos(columnAsientos);
        return this;
    }

    public void setColumnAsientos(Integer columnAsientos) {
        this.columnAsientos = columnAsientos;
    }

    public BigDecimal getPrecioEntrada() {
        return this.precioEntrada;
    }

    public Evento precioEntrada(BigDecimal precioEntrada) {
        this.setPrecioEntrada(precioEntrada);
        return this;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public String getEventoTipoNombre() {
        return this.eventoTipoNombre;
    }

    public Evento eventoTipoNombre(String eventoTipoNombre) {
        this.setEventoTipoNombre(eventoTipoNombre);
        return this;
    }

    public void setEventoTipoNombre(String eventoTipoNombre) {
        this.eventoTipoNombre = eventoTipoNombre;
    }

    public String getEventoTipoDescripcion() {
        return this.eventoTipoDescripcion;
    }

    public Evento eventoTipoDescripcion(String eventoTipoDescripcion) {
        this.setEventoTipoDescripcion(eventoTipoDescripcion);
        return this;
    }

    public void setEventoTipoDescripcion(String eventoTipoDescripcion) {
        this.eventoTipoDescripcion = eventoTipoDescripcion;
    }

    public Set<EventoIntegrante> getIntegrantes() {
        return this.integrantes;
    }

    public void setIntegrantes(Set<EventoIntegrante> eventoIntegrantes) {
        if (this.integrantes != null) {
            this.integrantes.forEach(i -> i.setEvento(null));
        }
        if (eventoIntegrantes != null) {
            eventoIntegrantes.forEach(i -> i.setEvento(this));
        }
        this.integrantes = eventoIntegrantes;
    }

    public Evento integrantes(Set<EventoIntegrante> eventoIntegrantes) {
        this.setIntegrantes(eventoIntegrantes);
        return this;
    }

    public Evento addIntegrantes(EventoIntegrante eventoIntegrante) {
        this.integrantes.add(eventoIntegrante);
        eventoIntegrante.setEvento(this);
        return this;
    }

    public Evento removeIntegrantes(EventoIntegrante eventoIntegrante) {
        this.integrantes.remove(eventoIntegrante);
        eventoIntegrante.setEvento(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evento)) {
            return false;
        }
        return getId() != null && getId().equals(((Evento) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Evento{" +
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
