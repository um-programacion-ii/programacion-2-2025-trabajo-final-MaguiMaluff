package um.backend.sales;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sale")
public class SaleEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String userId;

    @Column(nullable = false)
    private long eventoId;

    @Column(precision = 14, scale = 2)
    private BigDecimal totalAmount;

    private String externalSaleId; // id/numero que devuelva la cátedra, si corresponde

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleItemEntity> items = new ArrayList<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getEventoId() { return eventoId; }
    public void setEventoId(long eventoId) { this.eventoId = eventoId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getExternalSaleId() { return externalSaleId; }
    public void setExternalSaleId(String externalSaleId) { this.externalSaleId = externalSaleId; }
    public Instant getCreatedAt() { return createdAt; }
    public List<SaleItemEntity> getItems() { return items; }
    public void setItems(List<SaleItemEntity> items) { this.items = items; }
}