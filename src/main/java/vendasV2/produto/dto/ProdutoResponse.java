package vendasV2.produto.dto;

import vendasV2.produto.model.Produto;

import java.math.BigDecimal;

public record ProdutoResponse(Long id,
                              String nome,
                              BigDecimal valorUnitario,
                              Integer quantidadeEstoque) {

    public static ProdutoResponse to(Produto produto) {
        return new ProdutoResponse(produto.getId(),
                produto.getNome(),
                produto.getValor(),
                produto.getQuantidade());
    }
}
