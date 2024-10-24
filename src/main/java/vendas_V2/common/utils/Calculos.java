package vendas_V2.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vendas_V2.produto.repository.ProdutoRepository;
import vendas_V2.venda.model.Venda;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class Calculos {

    private final ProdutoRepository produtoRepository; // Repositório de Produto

    public BigDecimal calcularTotalVenda(BigDecimal valorProduto, Integer quantidade) {
        return BigDecimal.valueOf(quantidade).multiply(valorProduto);
    }

    public Venda construirVenda(Long vendedorId, Long produtoId, Integer quantidade, BigDecimal totalVenda) {
        return Venda.convert(vendedorId, produtoId, quantidade, totalVenda);
    }

}
