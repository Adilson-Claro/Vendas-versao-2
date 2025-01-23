package vendasV2.produto.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vendasV2.common.ExceptionsUtils.ProdutoNaoEncontradoException;
import vendasV2.common.ValidationsUtils.Validations;
import vendasV2.produto.dto.ProdutoRequest;
import vendasV2.produto.dto.ProdutoResponse;
import vendasV2.produto.model.Produto;
import vendasV2.produto.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @Test
    void testDeveSalvarListaDeProdutos() {
        var request = List.of(
                new ProdutoRequest(1L, "Camisa", new BigDecimal("100.0"), 10),
                new ProdutoRequest(2L, "Camisa verde", new BigDecimal("200.0"), 20)
        );

        var produto1 = new Produto(null, "Camisa", new BigDecimal("100.0"), 10);
        var produto2 = new Produto(null, "Camisa verde", new BigDecimal("200.0"), 20);

        when(produtoRepository.saveAll(anyList())).thenReturn(List.of(produto1, produto2));

        var produtosSalvos = produtoService.salvarListaProdutos(request);

        verify(produtoRepository).saveAll(anyList());

        assertNotNull(produtosSalvos);
        assertEquals(2, produtosSalvos.size());
        assertEquals("Camisa", produtosSalvos.get(0).getNome());
        assertEquals(new BigDecimal("100.0"), produtosSalvos.get(0).getValor());
        assertEquals(10, produtosSalvos.get(0).getQuantidade());

        assertEquals("Camisa verde", produtosSalvos.get(1).getNome());
        assertEquals(new BigDecimal("200.0"), produtosSalvos.get(1).getValor());
        assertEquals(20, produtosSalvos.get(1).getQuantidade());
    }

    @Test
    void testBuscarProdutoQuandoEncontrado() {
        var request = new ProdutoRequest(1L, "Camisa", new BigDecimal("100.0"), 10);

        var produtoEncontrado = new Produto(1L, "Camisa", new BigDecimal("100.0"), 10);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoEncontrado));

        List<ProdutoResponse> response = produtoService.buscarProduto(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Camisa", response.get(0).nome());
        assertEquals(new BigDecimal("100.0"), response.get(0).valorUnitario());
        assertEquals(10, response.get(0).quantidadeEstoque());

        verify(produtoRepository).findById(1L);
    }

    @Test
    void testBuscarProdutoQuandoNaoEncontrado() {
        var request = new ProdutoRequest(1L, "Camisa", new BigDecimal("100.0"), 10);

        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        var response = produtoService.buscarProduto(request);

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(produtoRepository).findById(1L);
    }

    @Test
    void testAlterarProduto() {
        var produtoOriginal = new Produto(1L, "Produto Original", new BigDecimal("50.00"), 15);

        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoOriginal);

        var produtoSalvo = produtoRepository.save(produtoOriginal);
        assertNotNull(produtoSalvo.getId());

        produtoSalvo.setNome("Produto Alterado");
        produtoSalvo.setValor(new BigDecimal("75.00"));
        produtoSalvo.setQuantidade(25);

        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoSalvo);

        var produtoAtualizado = produtoRepository.save(produtoSalvo);

        assertEquals("Produto Alterado", produtoAtualizado.getNome());
        assertEquals(new BigDecimal("75.00"), produtoAtualizado.getValor());
        assertEquals(25, produtoAtualizado.getQuantidade());
    }

}
