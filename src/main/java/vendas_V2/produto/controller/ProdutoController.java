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
    public ResponseEntity<ProdutoResponse> salvarProduto(@RequestBody @Valid ProdutoRequest request) {
        var produtoCriado = ProdutoResponse.convert(produtoService.salvarProduto(request));
        return ResponseEntity.ok(produtoCriado);
    }

    @RequestMapping
    public ResponseEntity<List<ProdutoResponse>> buscarProduto() {
        var produtos = produtoService.buscarProduto();
        return ResponseEntity.ok(produtos);
    }

    @PutMapping
    public ResponseEntity<ProdutoResponse> alterarProduto(@RequestBody @Valid ProdutoRequest request) {
        var produtoAlterado = ProdutoResponse.convert(produtoService.alterarProduto(request));
        return ResponseEntity.ok(produtoAlterado);
    }

}
