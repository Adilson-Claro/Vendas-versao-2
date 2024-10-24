package vendas_V2.venda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vendas_V2.venda.model.Venda;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByVendedorIdAndDataCadastroBetween(Long vendedorId, LocalDate startDate, LocalDate endDate);

    Optional<Venda> findById(Long aLong);
}
