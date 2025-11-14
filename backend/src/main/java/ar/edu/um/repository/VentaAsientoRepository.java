package ar.edu.um.repository;

import ar.edu.um.domain.VentaAsiento;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VentaAsiento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VentaAsientoRepository extends JpaRepository<VentaAsiento, Long> {}
