package um.backend.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventSyncRunner {

    private static final Logger log = LoggerFactory.getLogger(EventSyncRunner.class);

    @Bean
    ApplicationRunner syncOnStartup(EventSyncService sync) {
        return args -> sync.syncAllFromProxy()
                .doOnNext(cnt -> log.info("Evento sync on startup: {} eventos sincronizados"))
                .subscribe(
                        cnt -> log.info("Event sync completed. {} eventos", cnt),
                        err -> log.warn("Event sync failed at startup: {}", err.getMessage())
                );
    }
}