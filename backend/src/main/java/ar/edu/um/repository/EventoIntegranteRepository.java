package ar.edu.um.repository;

import ar.edu.um.domain.EventoIntegrante;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventoIntegrante entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventoIntegranteRepository extends JpaRepository<EventoIntegrante, Long> {}
