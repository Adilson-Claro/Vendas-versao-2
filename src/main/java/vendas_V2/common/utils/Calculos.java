package vendas_V2.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vendas_V2.produto.model.Produto;
import vendas_V2.produto.repository.ProdutoRepository;
import vendas_V2.venda.model.Venda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Calculos {

    private final ProdutoRepository produtoRepository; // Repositório de Produto

    // Método para calcular o total de uma venda
    public BigDecimal calcularTotalVenda(BigDecimal valorProduto, Integer quantidade) {
        return BigDecimal.valueOf(quantidade).multiply(valorProduto);
    }

    // Método para construir uma venda
    public Venda construirVenda(Long vendedorId, Long produtoId, Integer quantidade, BigDecimal totalVenda) {
        return Venda.convert(vendedorId, produtoId, quantidade, totalVenda);
    }

    // Método para calcular o total de vendas a partir de uma lista de vendas
    public BigDecimal calcularTotalVendas(List<Venda> vendas, Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado."));

        return vendas.stream()
                .map(venda -> calcularTotalVenda(produto.getValor(), venda.getQuantidade()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Método para calcular a quantidade total de vendas
    public Integer calcularQuantidadeTotal(List<Venda> vendas) {
        return vendas.stream()
                .mapToInt(Venda::getQuantidade)
                .sum();
    }

    // Método para calcular a média de vendas por vendedor
    public BigDecimal calcularMediaDeVendasPorVendedor(Long vendedorId, List<Venda> vendas, Long produtoId) {
        List<Venda> vendasDoVendedor = vendas.stream()
                .filter(venda -> venda.getVendedorId().equals(vendedorId)) // Filtra as vendas do vendedor específico
                .toList();

        BigDecimal totalVendas = calcularTotalVendas(vendasDoVendedor, produtoId);
        Integer quantidadeTotal = calcularQuantidadeTotal(vendasDoVendedor);

        if (quantidadeTotal == 0) {
            throw new NotFoundException("Não há vendas para o vendedor no período especificado.");
        }

        // Agora chamamos a função de cálculo da média
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado."));

        return calcularMediaDeVendas(produto.getValor(), quantidadeTotal, totalVendas);
    }

    // Método para calcular a média de vendas com base no valor do produto, quantidade e total
    public BigDecimal calcularMediaDeVendas(BigDecimal valorProduto, Integer quantidade, BigDecimal totalVendas) {
        return BigDecimal.valueOf(quantidade)
                .multiply(valorProduto)
                .divide(totalVendas, 2, RoundingMode.HALF_UP);
    }
}
