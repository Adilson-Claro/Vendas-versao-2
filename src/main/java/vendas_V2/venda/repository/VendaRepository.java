package vendas_V2.venda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vendas_V2.venda.model.Venda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByVendedorId(Long vendedorId);

    Optional<Venda> findById(Long id);

    List<Venda> findByVendedorIdAndDataCadastroBetween(Long idVendedor, LocalDateTime dataInicio, LocalDateTime dataFim);
}
