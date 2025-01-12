package vendasV2.vendedor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vendasV2.vendedor.model.Vendedor;

import java.util.Optional;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    Optional<Vendedor> findByCpf(String cpf);

    Optional<Vendedor> findById(Long id);
}
