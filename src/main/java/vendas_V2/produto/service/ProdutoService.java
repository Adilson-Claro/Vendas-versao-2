package vendas_V2.produto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.utils.NotFoundException;
import vendas_V2.common.utils.validations.Validations;
import vendas_V2.produto.dto.ProdutoRequest;
import vendas_V2.produto.dto.ProdutoResponse;
import vendas_V2.produto.model.Produto;
import vendas_V2.produto.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final Validations validations;

    public List<Produto> salvarListaProdutos(List<ProdutoRequest> request) {
        var listaProdutos = request.stream()
                .map(produtoRequest -> new Produto(null, produtoRequest.nome(), produtoRequest.valor(), produtoRequest.quantidade()))
                .collect(Collectors.toList());
        return produtoRepository.saveAll(listaProdutos);
    }

    private Produto construirProduto(String nome, BigDecimal valor, Integer quantidade) {

        return Produto.convert(null, nome, valor, quantidade);
    }

    public List<ProdutoResponse> buscarProduto() {

        var produto = produtoRepository.findAll();

        if (produto.isEmpty()) {
            throw new NotFoundException("Produto não encontrado");
        }

        return produto.stream()
                .map(ProdutoResponse::convert)
                .toList();
    }

    public Produto atualizarProduto(ProdutoRequest request) {

        var localizarProduto = produtoRepository.findById(request.id());

        if (produtoRepository.existsById(request.id())) {
            var alterarProduto = construirProduto(request.nome(), request.valor(), request.quantidade());

            produtoRepository.save(alterarProduto);
            return alterarProduto;
        }

        return null;
    }

    public void deletarProduto(Long id) {

        var produto = validations.verificarProdutoExistente(id);

        produtoRepository.deleteById(id);
    }

    public ProdutoResponse atualizarProduto(Long id, ProdutoRequest request) {

        var existProduto = validations.verificarProdutoExistente(id);

        existProduto.setNome(request.nome());
        existProduto.setValor(request.valor());

        var produtoAtualizado = produtoRepository.save(existProduto);

        return ProdutoResponse.convert(produtoAtualizado);
    }
}
