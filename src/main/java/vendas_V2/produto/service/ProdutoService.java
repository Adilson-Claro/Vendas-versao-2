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

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final Validations validations;

    public Produto salvarProduto(ProdutoRequest request) {

        var produto = construirProduto(request.nome(), request.valor());

        produtoRepository.save(produto);
        return produto;
    }

    private Produto construirProduto(String nome, BigDecimal valor) {

        return Produto.convert(null, nome, valor);
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
            var alterarProduto = construirProduto(request.nome(), request.valor());

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
