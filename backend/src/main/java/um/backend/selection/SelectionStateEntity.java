package um.backend.selection;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import um.backend.selection.converter.SelectedSeatListConverter;
import um.backend.selection.converter.StringListConverter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "selection_state", indexes = {
        @Index(name = "idx_selection_user_event", columnList = "userId,eventoId")
})
public class SelectionStateEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String userId;

    @Column(nullable = false)
    private long eventoId;

    @Convert(converter = SelectedSeatListConverter.class)
    @Column(columnDefinition = "json")
    private List<SelectedSeat> seats;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "json")
    private List<String> names;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SelectionStage stage;

    private Instant bloqueadoHasta;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Long version;

    // getters y setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getEventoId() { return eventoId; }
    public void setEventoId(long eventoId) { this.eventoId = eventoId; }

    public List<SelectedSeat> getSeats() { return seats; }
    public void setSeats(List<SelectedSeat> seats) { this.seats = seats; }

    public List<String> getNames() { return names; }
    public void setNames(List<String> names) { this.names = names; }

    public SelectionStage getStage() { return stage; }
    public void setStage(SelectionStage stage) { this.stage = stage; }

    public Instant getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(Instant bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}