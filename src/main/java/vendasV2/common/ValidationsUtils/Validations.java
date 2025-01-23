package vendasV2.common.ValidationsUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vendasV2.common.ExceptionsUtils.NotFoundException;
import vendasV2.common.ExceptionsUtils.ProdutoJaCadastradoException;
import vendasV2.common.ExceptionsUtils.QuantityInsufficientException;
import vendasV2.produto.dto.ProdutoRequest;
import vendasV2.produto.model.Produto;
import vendasV2.produto.repository.ProdutoRepository;
import vendasV2.venda.model.Venda;
import vendasV2.venda.repository.VendaRepository;
import vendasV2.vendedor.dto.VendedorRequest;
import vendasV2.vendedor.model.Vendedor;
import vendasV2.vendedor.repository.VendedorRepository;

import java.util.*;
import java.util.stream.Collectors;

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
            throw new QuantityInsufficientException("Quantidade em estoque insuficiente.");
        }
    }

    public void validarProdutosJaCadastrados(List<ProdutoRequest> produtos) {
        Set<String> listaProdutos = produtos.stream()
                .map(ProdutoRequest::nome)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!listaProdutos.isEmpty()) {
            List<Produto> produtosExistentes = produtoRepository.findByNomeIn(new ArrayList<>(listaProdutos));

            if (!produtosExistentes.isEmpty()) {
                Set<String> produtosExistentesNomes = produtosExistentes.stream()
                        .map(Produto::getNome)
                        .collect(Collectors.toSet());
                throw new NotFoundException("Os seguintes produtos já estão cadastrados: " + produtosExistentesNomes);
            }
        }
    }

    public void validarVendedoresJaCadastrados(List<VendedorRequest> vendedores) {

        Set<String> listaVendedores = vendedores.stream()
                .map(VendedorRequest::cpf)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!listaVendedores.isEmpty()) {

            List<Vendedor> vendedoresExistentes = vendedorRepository.findByCpfIn(new ArrayList<>(listaVendedores));

            if (!vendedoresExistentes.isEmpty()) {

                Set<String> vendedoresExistentesCpfs = vendedoresExistentes.stream()
                        .map(Vendedor::getCpf)
                        .collect(Collectors.toSet());

                throw new ProdutoJaCadastradoException("Os seguintes vendedores já estão cadastrados: " + vendedoresExistentesCpfs);
            }
        }
    }

    public void validarListaProdutos(List<ProdutoRequest> produtos) {
        Optional.ofNullable(produtos)
                .filter(v -> !v.isEmpty())
                .orElseThrow(() -> new IllegalStateException("A lista de produtos não pode ser vazia"));
    }

    public void validarVendedor(List<VendedorRequest> vendedores) {
        if (vendedores == null || vendedores.isEmpty()) {
            throw new IllegalArgumentException("A lista de vendedores não pode ser nula ou vazia");
        }

        for (VendedorRequest vendedor : vendedores) {
            validarNome(vendedor.nome());
            validarCpf(vendedor.cpf());
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
    }

    private void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("Cpf não pode ser nulo ou vazio");
        }
    }
}

