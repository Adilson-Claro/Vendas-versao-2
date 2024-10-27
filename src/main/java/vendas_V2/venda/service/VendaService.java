package vendas_V2.venda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.utils.Calculos;
import vendas_V2.common.utils.validations.Validations;
import vendas_V2.produto.dto.ProdutoResponse;
import vendas_V2.venda.dto.VendaRequest;
import vendas_V2.venda.dto.VendaResponse;
import vendas_V2.venda.dto.VendaResponseCompleta;
import vendas_V2.venda.repository.VendaRepository;
import vendas_V2.vendedor.dto.VendedorResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final Calculos calculos;
    private final Validations validations;

    public void salvarVenda(VendaRequest request) {
        var produto = validations.verificarProdutoExistente(request.produtoId());
        var vendedor = validations.verificarVendedorExistente(request.vendedorId());

        var venda = calculos.construirVenda(vendedor.getId(), produto.getId(), request.quantidade());
        vendaRepository.save(venda);
    }


    public List<VendaResponseCompleta> buscarVendas() {
        var vendas = vendaRepository.findAll();

        return vendas.stream()
                .map(venda -> {
                    var vendedor = validations.verificarVendedorExistente(venda.getVendedorId());
                    var produto = validations.verificarProdutoExistente(venda.getProdutoId());
                    var existVenda = validations.verificarVendaExistente(venda.getId());

                    var totalVendas = calcularTotalVendasPorVendedor(vendedor.getId());

                    double valorTotal = BigDecimal.valueOf(venda.getQuantidade())
                            .multiply(produto.getValor())
                            .doubleValue();

                    return VendaResponseCompleta.convert(
                            VendaResponse.convert(existVenda, totalVendas, valorTotal),
                            ProdutoResponse.convert(produto),
                            VendedorResponse.convert(vendedor)
                    );
                }).collect(Collectors.toList());
    }

    public void cancelarVenda(Long id) {

        var venda = validations.verificarVendaExistente(id);

        vendaRepository.deleteById(id);
    }

    public VendaResponseCompleta alterarVenda(Long id, VendaRequest vendaRequest) {
        var vendaExistente = validations.verificarVendaExistente(id);
        var vendedor = validations.verificarVendedorExistente(vendaExistente.getVendedorId());
        var produto = validations.verificarProdutoExistente(vendaExistente.getProdutoId());

        vendaExistente.setQuantidade(vendaRequest.quantidade());

        var vendaAtualizada = vendaRepository.save(vendaExistente);

        var totalVendas = calcularTotalVendasPorVendedor(vendedor.getId());

        double valorTotal = BigDecimal.valueOf(vendaAtualizada.getQuantidade())
                .multiply(produto.getValor())
                .doubleValue();

        return VendaResponseCompleta.convert(
                VendaResponse.convert(vendaAtualizada, totalVendas, valorTotal),
                ProdutoResponse.convert(produto),
                VendedorResponse.convert(vendedor)
        );
    }

    private Integer calcularTotalVendasPorVendedor(Long vendedorId) {
        var vendasDoVendedor = vendaRepository.findByVendedorId(vendedorId);
        return vendasDoVendedor.size();
    }
}

