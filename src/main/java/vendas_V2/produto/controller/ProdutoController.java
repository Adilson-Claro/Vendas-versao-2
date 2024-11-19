package vendas_V2.produto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vendas_V2.produto.dto.ProdutoRequest;
import vendas_V2.produto.dto.ProdutoResponse;
import vendas_V2.produto.service.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("produto")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<String> cadastrarProdutos(@RequestBody @Valid List<ProdutoRequest> request) {
        produtoService.salvarListaProdutos(request);
        return ResponseEntity.ok("Produtos cadastrados com sucesso!");
    }

    @RequestMapping
    public ResponseEntity<List<ProdutoResponse>> buscarProduto() {
        var produtos = produtoService.buscarProduto();
        return ResponseEntity.ok(produtos);
    }

    @PutMapping
    public ResponseEntity<ProdutoResponse> alterarProduto(@RequestBody @Valid ProdutoRequest request) {
        var produtoAlterado = ProdutoResponse.convert(produtoService.atualizarProduto(request));
        return ResponseEntity.ok(produtoAlterado);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<String> atualizarProduto(
            @PathVariable Long id,
            @RequestBody ProdutoRequest produtoRequest) {

        produtoService.atualizarProduto(id, produtoRequest);

        return ResponseEntity.ok("Dados do produto atualizado com sucesso!");
    }

}
