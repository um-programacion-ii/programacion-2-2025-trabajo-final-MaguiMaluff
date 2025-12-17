package um.backend.selection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SelectionService {

    final SelectionStateRepository repo;

    public SelectionService(SelectionStateRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public SelectionStateEntity getOrCreate(String userId, long eventoId) {
        Instant now = Instant.now();
        Optional<SelectionStateEntity> opt = repo.findFirstByUserIdAndEventoIdOrderByUpdatedAtDesc(userId, eventoId);
        if (opt.isPresent()) {
            SelectionStateEntity s = opt.get();
            if (s.getStage() == SelectionStage.BLOCKED && s.getBloqueadoHasta() != null && s.getBloqueadoHasta().isBefore(now)) {
                s.setStage(SelectionStage.SELECTING);
                s.setBloqueadoHasta(null);
                return repo.save(s);
            }
            return s;
        }
        SelectionStateEntity s = new SelectionStateEntity();
        s.setId(UUID.randomUUID());
        s.setUserId(userId);
        s.setEventoId(eventoId);
        s.setSeats(List.of());
        s.setNames(List.of());
        s.setStage(SelectionStage.SELECTING);
        s.setBloqueadoHasta(null);
        return repo.save(s);
    }

    @Transactional(readOnly = true)
    public SelectionStateEntity getById(UUID selectionId) {
        return repo.findById(selectionId).orElseThrow();
    }

    @Transactional
    public SelectionStateEntity updateSeats(UUID selectionId, List<SelectedSeat> seats) {
        SelectionStateEntity s = repo.findById(selectionId).orElseThrow();
        s.setSeats(seats == null ? List.of() : seats);
        if (s.getStage() == SelectionStage.BLOCKED) {
            s.setStage(SelectionStage.SELECTING);
            s.setBloqueadoHasta(null);
        }
        return repo.save(s);
    }

    @Transactional
    public SelectionStateEntity updateNames(UUID selectionId, List<String> names) {
        SelectionStateEntity s = repo.findById(selectionId).orElseThrow();
        s.setNames(names == null ? List.of() : names);
        if (!s.getNames().isEmpty()) {
            s.setStage(SelectionStage.FILLED_NAMES);
        } else if (s.getStage() == SelectionStage.FILLED_NAMES) {
            s.setStage(SelectionStage.SELECTING);
        }
        return repo.save(s);
    }

    @Transactional
    public SelectionStateEntity markBlocked(UUID selectionId, Instant bloqueadoHasta) {
        SelectionStateEntity s = repo.findById(selectionId).orElseThrow();
        s.setStage(SelectionStage.BLOCKED);
        s.setBloqueadoHasta(bloqueadoHasta);
        return repo.save(s);
    }

    public Instant defaultBlockTtlUntil() {
        return Instant.now().plus(5, ChronoUnit.MINUTES);
    }
}