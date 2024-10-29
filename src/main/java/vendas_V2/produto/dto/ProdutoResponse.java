package vendas_V2.produto.dto;

import vendas_V2.produto.model.Produto;

import java.math.BigDecimal;

public record ProdutoResponse(Long id,
                              String nome,
                              BigDecimal valorUnitario,
                              Integer quantidade) {

    public static ProdutoResponse convert(Produto produto) {
        return new ProdutoResponse(produto.getId(),
                produto.getNome(),
                produto.getValor(),
                produto.getQuantidade());
    }
}
