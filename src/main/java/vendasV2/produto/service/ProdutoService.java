package vendasV2.produto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendasV2.common.ExceptionsUtils.NotFoundException;
import vendasV2.common.ValidationsUtils.Validations;
import vendasV2.produto.dto.ProdutoRequest;
import vendasV2.produto.dto.ProdutoResponse;
import vendasV2.produto.model.Produto;
import vendasV2.produto.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final Validations validations;

    public void salvarListaProdutos(List<ProdutoRequest> produtos) {

        List<String> nomeProdutos = produtos.stream()
                .map(ProdutoRequest::nome)
                .filter(Objects::nonNull)
                .toList();

        if (!nomeProdutos.isEmpty()) {
            List<Produto> produtosExistentes = produtoRepository.findByNomeIn(nomeProdutos);

            if (!produtosExistentes.isEmpty()) {
                throw new NotFoundException("Os seguintes produtos já estão cadastrados: " + nomeProdutos);
            }
        }

        List<Produto> listaProdutos = produtos.stream()
                .map(produtoRequest -> Produto.convert(
                        null,
                        produtoRequest.nome(),
                        produtoRequest.valor(),
                        produtoRequest.quantidade()
                ))
                .toList();

        produtoRepository.saveAll(listaProdutos);
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

        produtoRepository.findById(request.id());

        if (produtoRepository.existsById(request.id())) {
            var alterarProduto = construirProduto(request.nome(), request.valor(), request.quantidade());

            produtoRepository.save(alterarProduto);
            return alterarProduto;
        }

        return null;
    }

    public void deletarProduto(Long id) {

        validations.verificarProdutoExistente(id);

        produtoRepository.deleteById(id);
    }

    public void atualizarProduto(Long id, ProdutoRequest request) {

        var existProduto = validations.verificarProdutoExistente(id);

        existProduto.setNome(request.nome());
        existProduto.setValor(request.valor());

        var produtoAtualizado = produtoRepository.save(existProduto);

        ProdutoResponse.convert(produtoAtualizado);
    }
}
