package um.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.backend.selection.SelectionService;
import um.backend.selection.SelectionStage;
import um.backend.selection.SelectionStateRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository repo;
    private final SelectionStateRepository selectionRepo;
    private final int ttlMinutes;

    public SessionService(SessionRepository repo,
                          SelectionStateRepository selectionRepo,
                          @Value("${security.session.ttl-minutes:30}") int ttlMinutes) {
        this.repo = repo;
        this.selectionRepo = selectionRepo;
        this.ttlMinutes = ttlMinutes;
    }

    @Transactional
    public SessionEntity create(String userId) {
        Instant now = Instant.now();
        SessionEntity s = new SessionEntity();
        s.setId(UUID.randomUUID());
        s.setUserId(userId);
        s.setLastActivity(now);
        s.setExpiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES));
        s.setActive(true);
        return repo.save(s);
    }

    // Actualiza actividad y verifica expiración; si expiró, invalida y limpia selección
    @Transactional
    public boolean touchAndValidate(String userId) {
        Instant now = Instant.now();
        var opt = repo.findFirstByUserIdAndActiveOrderByLastActivityDesc(userId, true);
        if (opt.isEmpty()) return false; // sin sesión activa → requiere login
        SessionEntity s = opt.get();
        if (s.getExpiresAt().isBefore(now)) {
            // expirada: desactiva y limpia selección para comenzar en paso 1
            s.setActive(false);
            repo.save(s);
            resetSelectionForUser(userId);
            return false;
        }
        s.setLastActivity(now);
        s.setExpiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES));
        repo.save(s);
        return true;
    }

    @Transactional
    public void logoutAll(String userId) {
        List<SessionEntity> sessions = repo.findByUserId(userId);
        for (SessionEntity s : sessions) {
            s.setActive(false);
            repo.save(s);
        }
        // Al cerrar sesión explícita, invalidar proceso
        resetSelectionForUser(userId);
    }

    private void resetSelectionForUser(String userId) {
        // Política simple: limpiar selección (empezar en paso 1)
        selectionRepo.findAll().stream()
                .filter(e -> userId.equals(e.getUserId()))
                .forEach(e -> {
                    e.setStage(SelectionStage.SELECTING);
                    e.setBloqueadoHasta(null);
                    e.setSeats(List.of());
                    e.setNames(List.of());
                    selectionRepo.save(e);
                });
    }
}