package vendasV2.produto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendasV2.common.ValidationsUtils.Validations;
import vendasV2.produto.dto.ProdutoRequest;
import vendasV2.produto.dto.ProdutoResponse;
import vendasV2.produto.model.Produto;
import vendasV2.produto.repository.ProdutoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final Validations validations;

    public void salvarListaProdutos(List<ProdutoRequest> produtos) {

        validations.validarProdutosJaCadastrados(produtos);
        validations.validarListaProdutos(produtos);

        List<Produto> listaProdutos = produtos.stream()
                .map(produtoRequest -> Produto.convert(
                        null,
                        produtoRequest.nome(),
                        produtoRequest.valor(),
                        produtoRequest.quantidade()
                )).toList();

        produtoRepository.saveAll(listaProdutos);
    }

    public List<ProdutoResponse> buscarProduto(ProdutoRequest request) {

        var produto = produtoRepository.findById(request.id());

        return produto.stream()
                .map(ProdutoResponse::convert)
                .toList();
    }

    public void atualizarProduto(Long id, ProdutoRequest request) {

        var produtoExistente = validations.verificarProdutoExistente(id);

        produtoExistente.setNome(request.nome());
        produtoExistente.setValor(request.valor());

        var produtoAtualizado = produtoRepository.save(produtoExistente);

        ProdutoResponse.convert(produtoAtualizado);
    }
    public void deletarProduto(Long id) {

        validations.verificarProdutoExistente(id);

        produtoRepository.deleteById(id);
    }
}
