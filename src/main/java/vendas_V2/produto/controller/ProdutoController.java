package vendas_V2.produto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vendas_V2.produto.dto.ProdutoRequest;
import vendas_V2.produto.dto.ProdutoResponse;
import vendas_V2.produto.repository.ProdutoRepository;
import vendas_V2.produto.service.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("produto")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;

    @PostMapping
    public void cadastrarProdutos(@RequestBody @Valid List<ProdutoRequest> request) {
        produtoService.salvarListaProdutos(request);
    }

    @GetMapping
    public Page<ProdutoResponse> buscarProduto(Pageable produtos) {
        return produtoRepository.findAll(produtos).map(ProdutoResponse::convert);
    }

    @DeleteMapping("{id}")
    public void deletarProduto(@PathVariable Long id) {
        produtoRepository.deleteById(id);
    }

    @PutMapping("{id}")
    public void atualizarProduto(@PathVariable Long id, @RequestBody ProdutoRequest produtoRequest) {
        produtoService.atualizarProduto(id, produtoRequest);
    }

}
