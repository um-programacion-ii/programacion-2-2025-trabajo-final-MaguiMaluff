package um.backend.security;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "session")
public class SessionEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String userId;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastActivity;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean active;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivity() { return lastActivity; }
    public void setLastActivity(Instant lastActivity) { this.lastActivity = lastActivity; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}