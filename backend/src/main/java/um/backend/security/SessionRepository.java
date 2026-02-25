package um.backend.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    Optional<SessionEntity> findFirstByUserIdAndActiveOrderByLastActivityDesc(String userId, boolean active);
    List<SessionEntity> findByUserId(String userId);
}