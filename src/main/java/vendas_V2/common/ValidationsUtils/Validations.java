package vendas_V2.common.ValidationsUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vendas_V2.common.ExceptionsUtils.NotFoundException;
import vendas_V2.produto.model.Produto;
import vendas_V2.produto.repository.ProdutoRepository;
import vendas_V2.venda.model.Venda;
import vendas_V2.venda.repository.VendaRepository;
import vendas_V2.vendedor.model.Vendedor;
import vendas_V2.vendedor.repository.VendedorRepository;

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

    public Vendedor verficarStatusVendedorAtivo(Long vendedorId) {
        return vendedorRepository.findById(vendedorId)
                .filter(vendedor -> vendedor.getStatus() != Vendedor.statusVendedor.INATIVO)
                .orElseThrow(() -> new NotFoundException("Vendedor INATIVO"));
    }

    public Vendedor verficarStatusVendedorInativo(Long vendedorId) {
        return vendedorRepository.findById(vendedorId)
                .filter(vendedor -> vendedor.getStatus() != Vendedor.statusVendedor.ATIVO)
                .orElseThrow(() -> new NotFoundException("Vendedor ATIVO"));
    }

    public Venda vendaStatus(Long vendaId) {
        return vendaRepository.findById(vendaId)
                .filter(venda -> venda.getStatus() == Venda.statusVenda.APROVADO)
                .orElseThrow(() -> new NotFoundException("Venda já aprovada"));
    }

    public void verificarQuantidadeEstoque(Produto produto, int quantidadeRequisitada) {
        if (produto.getQuantidade() < quantidadeRequisitada) {
            throw new NotFoundException("Quantidade em estoque insuficiente.");
        }
    }
}

