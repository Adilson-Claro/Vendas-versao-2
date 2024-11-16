package vendas_V2.venda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.utils.Calculos;
import vendas_V2.common.utils.NotFoundException;
import vendas_V2.common.utils.validations.Validations;
import vendas_V2.produto.dto.ProdutoResponse;
import vendas_V2.produto.repository.ProdutoRepository;
import vendas_V2.venda.dto.VendaRequest;
import vendas_V2.venda.dto.VendaResponse;
import vendas_V2.venda.dto.VendaResponseCompleta;
import vendas_V2.venda.dto.VendasPorPeriodoRequest;
import vendas_V2.venda.model.Venda;
import vendas_V2.venda.repository.VendaRepository;
import vendas_V2.vendedor.dto.VendedorResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final Calculos calculos;
    private final Validations validations;
    private final ProdutoRepository produtoRepository;

    public void salvarVenda(VendaRequest request) {
        var produto = validations.verificarProdutoExistente(request.produtoId());
        var vendedor = validations.verificarVendedorExistente(request.vendedorId());
        validations.verficarStatusVendedorAtivo(request.vendedorId());
        validations.verificarQuantidadeEstoque(produto, request.quantidade());

        var venda = calculos.construirVenda(vendedor.getId(), produto.getId(), request.quantidade());
        venda.setStatus(Venda.statusVenda.ANDAMENTO);
        vendaRepository.save(venda);
    }

    public Venda buscarVendaPorId(Long vendaId) {
        return validations.buscarVendaPorId(vendaId);
    }

    public Venda aprovarVenda(Long vendaId) {
        Venda venda = buscarVendaPorId(vendaId);

        validations.vendaStatus(vendaId);

        var produto = validations.verificarProdutoExistente(venda.getProdutoId());
        produto.setQuantidade(produto.getQuantidade() - venda.getQuantidade());
        produtoRepository.save(produto);

        venda.setStatus(Venda.statusVenda.APROVADO);
        return vendaRepository.save(venda);
    }

    public void cancelarVenda(Long vendaId) {
        var optionalVenda = vendaRepository.findById(vendaId);
        if (optionalVenda.isPresent()) {
            var venda = optionalVenda.get();
            var produto = validations.verificarProdutoExistente(venda.getProdutoId());

            if (venda.getStatus() == Venda.statusVenda.APROVADO) {
                produto.setQuantidade(produto.getQuantidade() + venda.getQuantidade());
                produtoRepository.save(produto);
            }

            venda.setStatus(Venda.statusVenda.CANCELADO);
            vendaRepository.save(venda);
        } else {
            validations.verificarVendaExistente(vendaId);
        }
    }

    public List<VendaResponseCompleta> buscarVendasPorVendedor(Long vendedorId) {

        var vendas = vendaRepository.findByVendedorId(vendedorId);

        return vendas.stream()
                .map(venda -> {
                    var vendedor = validations.verificarVendedorExistente(venda.getVendedorId());
                    var produto = validations.verificarProdutoExistente(venda.getProdutoId());
                    var vendaExistente = validations.verificarVendaExistente(venda.getId());

                    var totalVendas = calculos.calcularTotalVendasPorVendedor(vendedor.getId());

                    var valorTotal = calculos.calcularValorTotal(venda.getQuantidade(), produto.getValor());

                    var mediaVendas = calculos.calcularMediaVendas(valorTotal, totalVendas);

                    return VendaResponseCompleta.convert(
                            VendaResponse.convert(vendaExistente, totalVendas, valorTotal, mediaVendas),
                            ProdutoResponse.convert(produto),
                            VendedorResponse.convert(vendedor)
                    );
                }).collect(Collectors.toList());
    }

    public VendaResponseCompleta alterarVenda(Long id, VendaRequest vendaRequest) {
        var vendaExistente = validations.verificarVendaExistente(id);
        var vendedor = validations.verificarVendedorExistente(vendaExistente.getVendedorId());
        var produto = validations.verificarProdutoExistente(vendaExistente.getProdutoId());

        vendaExistente.setQuantidade(vendaRequest.quantidade());

        var vendaAtualizada = vendaRepository.save(vendaExistente);

        var totalVendas = calculos.calcularTotalVendasPorVendedor(vendedor.getId());

        var valorTotal = calculos.calcularValorTotal(vendaAtualizada.getQuantidade(), produto.getValor());

        var mediaVendas = calculos.calcularMediaVendas(valorTotal, totalVendas);

        return VendaResponseCompleta.convert(
                VendaResponse.convert(vendaAtualizada, totalVendas, valorTotal, mediaVendas),
                ProdutoResponse.convert(produto),
                VendedorResponse.convert(vendedor)
        );
    }

    public List<VendaResponseCompleta> buscarVendasPorPeriodo(Long idVendedor, VendasPorPeriodoRequest request) {
        var vendedor = validations.verificarVendedorExistente(idVendedor);
        var dataInicio = request.getDataInicio().atStartOfDay();
        var dataFim = request.getDataFim().atTime(23, 59, 59);

        var vendas = vendaRepository.findByVendedorIdAndDataCadastroBetween(idVendedor, dataInicio, dataFim);

        if (vendas.isEmpty()) {
            throw new NotFoundException("Nenhuma venda encontrada para este vendedor no período especificado.");
        }

        var valorTotal = vendas.stream()
                .mapToDouble(venda -> {
                    var produto = validations.verificarProdutoExistente(venda.getProdutoId());
                    return calculos.calcularValorTotal(venda.getQuantidade(), produto.getValor());
                })
                .sum();

        var diasNoPeriodo = ChronoUnit.DAYS.between(request.getDataInicio(), request.getDataFim()) + 1;
        var mediaVendas = diasNoPeriodo > 0 ? valorTotal / diasNoPeriodo : 0;

        var valorTotalArredondado = BigDecimal.valueOf(valorTotal).setScale(2, RoundingMode.HALF_UP);
        var mediaVendasArredondada = BigDecimal.valueOf(mediaVendas).setScale(2, RoundingMode.HALF_UP);

        return vendas.stream()
                .map(venda -> {
                    var produto = validations.verificarProdutoExistente(venda.getProdutoId());
                    return VendaResponseCompleta.convert(
                            VendaResponse.convert(venda, vendas.size(), valorTotalArredondado.doubleValue(), mediaVendasArredondada.doubleValue()),
                            ProdutoResponse.convert(produto),
                            VendedorResponse.convert(vendedor)
                    );
                })
                .collect(Collectors.toList());
    }

}

