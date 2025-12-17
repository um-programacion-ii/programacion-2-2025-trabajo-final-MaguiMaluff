package um.backend.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupDbChecker {

    private static final Logger log = LoggerFactory.getLogger(StartupDbChecker.class);
    private final JdbcTemplate jdbc;

    public StartupDbChecker(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void check() {
        try {
            Integer tableCount = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()", Integer.class);
            log.info("Startup DB check: schema={} tableCount={}", jdbc.queryForObject("SELECT DATABASE()", String.class), tableCount);
        } catch (Exception e) {
            log.warn("Startup DB check failed: {}", e.getMessage());
        }
    }
}