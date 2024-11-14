package vendas_V2.venda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.utils.Calculos;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        var vendedorStatus = validations.verficarStatusVendedor(request.vendedorId());
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

        var statusVenda = validations.vendaStatus(vendaId);

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
            var procurarVenda = validations.verificarVendaExistente(vendaId);
        }
    }

    public List<VendaResponseCompleta> buscarVendas() {
        var vendas = vendaRepository.findAll();

        return vendas.stream()
                .map(venda -> {
                    var vendedor = validations.verificarVendedorExistente(venda.getVendedorId());
                    var produto = validations.verificarProdutoExistente(venda.getProdutoId());
                    var existVenda = validations.verificarVendaExistente(venda.getId());

                    var totalVendas = calculos.calcularTotalVendasPorVendedor(vendedor.getId());

                    var valorTotal = calculos.calcularValorTotal(venda.getQuantidade(), produto.getValor());

                    var mediaVendas = calculos.calcularMediaVendas(valorTotal, totalVendas);

                    return VendaResponseCompleta.convert(
                            VendaResponse.convert(existVenda, totalVendas, valorTotal, mediaVendas),
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

    public List<VendaResponseCompleta> buscarVendasPorPeriodo(VendasPorPeriodoRequest request) {
        var dataInicio = request.getDataInicio();
        var dataFim = request.getDataFim();

        var vendas = vendaRepository.findAllByDataCadastroBetween(dataInicio.atStartOfDay(), dataFim.atTime(23, 59, 59));

        Map<Long, List<Venda>> vendasPorVendedor = vendas.stream()
                .collect(Collectors.groupingBy(Venda::getVendedorId));

        List<VendaResponseCompleta> resultado = new ArrayList<>();

        // Processar cada grupo de vendas por vendedor, manipula mapa
        for (Map.Entry<Long, List<Venda>> entry : vendasPorVendedor.entrySet()) {
            var vendedorId = entry.getKey();
            var vendasDoVendedor = entry.getValue();

            var valorTotal = 0;

            for (Venda venda : vendasDoVendedor) {
                var produto = validations.verificarProdutoExistente(venda.getProdutoId());
                valorTotal += calculos.calcularValorTotal(venda.getQuantidade(), produto.getValor());
            }

            var diasNoPeriodo = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
            var mediaVendas = (diasNoPeriodo > 0) ? valorTotal / diasNoPeriodo : 0;

            var valorTotalArredondado = BigDecimal.valueOf(valorTotal).setScale(2, RoundingMode.HALF_UP);
            var mediaVendasArredondada = BigDecimal.valueOf(mediaVendas).setScale(2, RoundingMode.HALF_UP);

            var vendedor = validations.verificarVendedorExistente(vendedorId);

            for (var venda : vendasDoVendedor) {
                var produto = validations.verificarProdutoExistente(venda.getProdutoId());

                resultado.add(VendaResponseCompleta.convert(
                        VendaResponse.convert(venda, vendasDoVendedor.size(), valorTotalArredondado.doubleValue(), mediaVendasArredondada.doubleValue()),
                        ProdutoResponse.convert(produto),
                        VendedorResponse.convert(vendedor)
                ));
            }
        }

        return resultado;
    }
}

