package um.backend.selection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SelectionStateRepository extends JpaRepository<SelectionStateEntity, UUID> {
    Optional<SelectionStateEntity> findFirstByUserIdAndEventoIdOrderByUpdatedAtDesc(String userId, long eventoId);
}