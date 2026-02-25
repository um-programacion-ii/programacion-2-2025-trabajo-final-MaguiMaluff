package um.backend.sales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleRepository extends JpaRepository<SaleEntity, UUID> {
}