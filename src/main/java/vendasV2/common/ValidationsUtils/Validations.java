package vendasV2.common.ValidationsUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vendasV2.common.ExceptionsUtils.NotFoundException;
import vendasV2.produto.model.Produto;
import vendasV2.produto.repository.ProdutoRepository;
import vendasV2.venda.model.Venda;
import vendasV2.venda.repository.VendaRepository;
import vendasV2.vendedor.repository.VendedorRepository;
import vendasV2.vendedor.model.Vendedor;

@Component
@RequiredArgsConstructor
public class Validations {

    private final ProdutoRepository produtoRepository;
    private final VendedorRepository vendedorRepository;
    private final VendaRepository vendaRepository;

    public Produto verificarProdutoExistente(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado."));
    }

    public Vendedor verificarVendedorExistente(Long id) {
        return vendedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vendedor não encontrado."));
    }

    public Venda verificarVendaExistente(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada"));
    }

    public Venda buscarVendaPorId(Long vendaId) {
        return vendaRepository.findById(vendaId)
                .orElseThrow(() -> new NotFoundException("Venda não encontrada para o ID: " + vendaId));
    }

    public void verficarStatusVendedorAtivo(Long vendedorId) {
        vendedorRepository.findById(vendedorId)
                .filter(vendedor -> vendedor.getStatus() != Vendedor.statusVendedor.INATIVO)
                .orElseThrow(() -> new IllegalStateException("Vendedor INATIVO"));
    }

    public Vendedor verficarStatusVendedorInativo(Long vendedorId) {
        return vendedorRepository.findById(vendedorId)
                .filter(vendedor -> vendedor.getStatus() != Vendedor.statusVendedor.ATIVO)
                .orElseThrow(() -> new IllegalStateException("Vendedor ATIVO"));
    }

    public void verificarQuantidadeEstoque(Produto produto, int quantidadeRequisitada) {
        if (produto.getQuantidade() < quantidadeRequisitada) {
            throw new NotFoundException("Quantidade em estoque insuficiente.");
        }
    }
}

