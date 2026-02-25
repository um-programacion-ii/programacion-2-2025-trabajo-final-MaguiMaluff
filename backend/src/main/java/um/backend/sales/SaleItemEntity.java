package um.backend.sales;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "sale_item")
public class SaleItemEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private SaleEntity sale;

    private int fila;
    private int columna;
    private String nombre; // nombre asignado a la entrada

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SaleEntity getSale() { return sale; }
    public void setSale(SaleEntity sale) { this.sale = sale; }
    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }
    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}