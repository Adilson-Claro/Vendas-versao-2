package vendas_V2.produto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vendas_V2.produto.model.Produto;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findById(Long aLong);
}
