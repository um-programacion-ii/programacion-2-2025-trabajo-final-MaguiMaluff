package um.backend.events;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "event")
public class EventEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String resumen;

    @Column(length = 4000)
    private String descripcion;

    private Instant fecha;

    private String direccion;

    @Column(length = 2000)
    private String imagen;

    private Integer filaAsientos;
    private Integer columnAsientos;

    @Column(precision = 14, scale = 2)
    private BigDecimal precioEntrada;

    // Datos de tipo de evento (plano para simplificar)
    private String tipoNombre;
    private String tipoDescripcion;

    // Opcional: estado/cancelado/expirado si luego lo necesitás
    private String estado;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Instant getFecha() { return fecha; }
    public void setFecha(Instant fecha) { this.fecha = fecha; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public Integer getFilaAsientos() { return filaAsientos; }
    public void setFilaAsientos(Integer filaAsientos) { this.filaAsientos = filaAsientos; }
    public Integer getColumnAsientos() { return columnAsientos; }
    public void setColumnAsientos(Integer columnAsientos) { this.columnAsientos = columnAsientos; }
    public BigDecimal getPrecioEntrada() { return precioEntrada; }
    public void setPrecioEntrada(BigDecimal precioEntrada) { this.precioEntrada = precioEntrada; }
    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { this.tipoNombre = tipoNombre; }
    public String getTipoDescripcion() { return tipoDescripcion; }
    public void setTipoDescripcion(String tipoDescripcion) { this.tipoDescripcion = tipoDescripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}