package vendasV2.produto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vendasV2.produto.dto.ProdutoRequest;
import vendasV2.produto.dto.ProdutoResponse;
import vendasV2.produto.repository.ProdutoRepository;
import vendasV2.produto.service.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("produto")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
