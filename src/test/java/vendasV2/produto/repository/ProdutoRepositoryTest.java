package vendasV2.produto.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import vendasV2.produto.dto.ProdutoRequest;
import vendasV2.produto.model.Produto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void testDeveSalvarProduto() {
        var produtoRequest = new ProdutoRequest(1L, "Test", new BigDecimal("123.90"),10);

        var saveProduto = new Produto(produtoRequest.id(), produtoRequest.nome(),
                produtoRequest.valor(), produtoRequest.quantidade());

        assertNotNull(saveProduto);
        assertEquals(produtoRequest.nome(), saveProduto.getNome());
        assertEquals(produtoRequest.valor(), saveProduto.getValor());
        assertEquals(produtoRequest.quantidade(), saveProduto.getQuantidade());
    }

    @Test
    void testDeletarProduto() {

        var produto = new Produto(null, "Test Produto", new BigDecimal("99.99"), 20);
        var produtoSalvo = produtoRepository.save(produto);

        assertNotNull(produtoSalvo.getId());

        produtoRepository.delete(produtoSalvo);

        var produtoOptional = produtoRepository.findById(produtoSalvo.getId());
        assertTrue(produtoOptional.isEmpty());
    }

    @Test
    void testAlterarProduto() {

        var produto = new Produto(null, "Produto Original", new BigDecimal("50.00"), 15);
        var produtoSalvo = produtoRepository.save(produto);

        assertNotNull(produtoSalvo.getId());

        produtoSalvo.setNome("Produto Alterado");
        produtoSalvo.setValor(new BigDecimal("75.00"));
        produtoSalvo.setQuantidade(25);

        var produtoAtualizado = produtoRepository.save(produtoSalvo);

        assertEquals("Produto Alterado", produtoAtualizado.getNome());
        assertEquals(new BigDecimal("75.00"), produtoAtualizado.getValor());
        assertEquals(25, produtoAtualizado.getQuantidade());
    }

    @Test
    void testRetornarListaDeProdutos() {
        var produto = new Produto(null, "Mouse",
                new BigDecimal("50.00"), 15);
        var produto1 = new Produto(null, "Teclado",
                new BigDecimal("70.00"), 10);

        produtoRepository.save(produto);
        produtoRepository.save(produto1);

        List<Produto> produtos = produtoRepository.findAll();

        assertNotNull(produtos);
        assertEquals(2, produtos.size());
    }

    @Test
    void testDeveRetornarProdutoPorId() {
        var produto = new Produto(null, "Mouse",
                new BigDecimal("50.00"), 15);

        var produtoSalvo = produtoRepository.save(produto);

        produtoRepository.findById(produtoSalvo.getId());

        assertNotNull(produtoSalvo);
        assertEquals(produto.getId(), produtoSalvo.getId());
        assertEquals(produto.getNome(), produtoSalvo.getNome());
    }

    @Test
    void testDeveRetornarProdutoPorNome() {
        var produto = new Produto(null, "Mouse",
                new BigDecimal("50.00"), 15);

        var produtoSalvo = produtoRepository.save(produto);

        produtoRepository.findByNomeIn(Collections.singletonList(produtoSalvo.getNome()));

        assertNotNull(produtoSalvo);
        assertEquals(produto.getId(), produtoSalvo.getId());
        assertEquals(produto.getNome(), produtoSalvo.getNome());
    }

    @Test
    void testDeveRetornarErroAoCadastrarProdutoComNomeNull() {
        var produto = new Produto(null, null, new BigDecimal("50.00"), 15);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            produtoRepository.save(produto);
            produtoRepository.flush();
        });
    }

    @Test
    void testDeveRetornarErroAoCadastrarProdutoComValorNull() {
        var produto = new Produto(null, "Mouse", null, 15);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            produtoRepository.save(produto);
            produtoRepository.flush();
        });
    }

    @Test
    void testDeveRetornarErroAoCadastrarProdutoComQuantidadeNull() {
        var produto = new Produto(null, "Mouse", new BigDecimal("54.90"), null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            produtoRepository.save(produto);
            produtoRepository.flush();
        });
    }

}
