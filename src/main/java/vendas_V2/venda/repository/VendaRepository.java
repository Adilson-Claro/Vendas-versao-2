package vendas_V2.venda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vendas_V2.venda.model.Venda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Query("SELECT COUNT(v) FROM Venda v WHERE v.vendedor.id = :vendedorId")
    Integer calcularTotalVendasPorVendedor(Long vendedorId);

    @Query("SELECT (p.valor * :quantidade) FROM Produto p WHERE p.id = :produtoId")
    Double calcularValorTotal(@Param("quantidade") Integer quantidade, @Param("produtoId") Long produtoId);

    @Query("SELECT (SUM(v.valorTotal) / COUNT(v)) FROM Venda v WHERE v.vendedor.id = :vendedorId")
    Double calcularMediaVendas(@Param("vendedorId") Long vendedorId);

    List<Venda> findByVendedorId(Long vendedorId);

    Optional<Venda> findById(Long id);

    List<Venda> findByVendedorIdAndDataCadastroBetween(Long idVendedor, LocalDateTime dataInicio, LocalDateTime dataFim);
}
