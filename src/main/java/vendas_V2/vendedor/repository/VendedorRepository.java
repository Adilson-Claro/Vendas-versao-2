package vendas_V2.vendedor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vendas_V2.vendedor.model.Vendedor;

import java.util.Optional;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {
    Optional<Vendedor> findById(Long id);
}
