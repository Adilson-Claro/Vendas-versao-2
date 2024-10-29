package vendas_V2.venda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.utils.Calculos;
import vendas_V2.common.utils.validations.Validations;
import vendas_V2.produto.dto.ProdutoResponse;
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

    public void salvarVenda(VendaRequest request) {
        var produto = validations.verificarProdutoExistente(request.produtoId());
        var vendedor = validations.verificarVendedorExistente(request.vendedorId());

        var venda = calculos.construirVenda(vendedor.getId(), produto.getId(), request.quantidade());
        vendaRepository.save(venda);
    }

    public Venda buscarVendaPorId(Long vendaId) {
        return validations.buscarVendaPorId(vendaId);
    }

    public Venda aprovarVenda(Long vendaId) {
        Venda venda = buscarVendaPorId(vendaId);
        venda.setStatus(Venda.statusVenda.APROVADO);
        return vendaRepository.save(venda);
    }

    public Venda cancelarVenda(Long vendaId) {
        Venda venda = buscarVendaPorId(vendaId);
        venda.setStatus(Venda.statusVenda.CANCELADO);
        return vendaRepository.save(venda);
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

        // Atualiza a quantidade da venda existente
        vendaExistente.setQuantidade(vendaRequest.quantidade());

        // Salva a venda atualizada
        var vendaAtualizada = vendaRepository.save(vendaExistente);

        // Calcula o total de vendas do vendedor
        var totalVendas = calculos.calcularTotalVendasPorVendedor(vendedor.getId());

        // Calcula o valorUnitario total da venda
        var valorTotal = calculos.calcularValorTotal(vendaAtualizada.getQuantidade(), produto.getValor());

        // Utiliza o método calcularMediaVendas para obter a média
        var mediaVendas = calculos.calcularMediaVendas(valorTotal, totalVendas);

        // Retorna a resposta completa com valor total e média
        return VendaResponseCompleta.convert(
                VendaResponse.convert(vendaAtualizada, totalVendas, valorTotal, mediaVendas), // Passa valor total e média
                ProdutoResponse.convert(produto),
                VendedorResponse.convert(vendedor)
        );
    }

    public List<VendaResponseCompleta> buscarVendasPorPeriodo(VendasPorPeriodoRequest request) {
        var dataInicio = request.getDataInicio();
        var dataFim = request.getDataFim();

        // Buscar vendas no período especificado
        var vendas = vendaRepository.findAllByDataCadastroBetween(dataInicio.atStartOfDay(), dataFim.atTime(23, 59, 59));

        // Agrupar vendas por vendedor
        Map<Long, List<Venda>> vendasPorVendedor = vendas.stream()
                .collect(Collectors.groupingBy(Venda::getVendedorId));

        List<VendaResponseCompleta> resultado = new ArrayList<>();

        // Processar cada grupo de vendas por vendedor
        for (Map.Entry<Long, List<Venda>> entry : vendasPorVendedor.entrySet()) {
            Long vendedorId = entry.getKey();
            List<Venda> vendasDoVendedor = entry.getValue();

            var valorTotal = 0;

            // Calcular o valor total das vendas para o vendedor
            for (Venda venda : vendasDoVendedor) {
                var produto = validations.verificarProdutoExistente(venda.getProdutoId());
                valorTotal += calculos.calcularValorTotal(venda.getQuantidade(), produto.getValor());
            }

            // Calcular a média de vendas considerando o número de dias no período
            var diasNoPeriodo = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1; // +1 para incluir o último dia
            var mediaVendas = (diasNoPeriodo > 0) ? valorTotal / diasNoPeriodo : 0;

            // Arredondar os valores para duas casas decimais
            var valorTotalArredondado = BigDecimal.valueOf(valorTotal).setScale(2, RoundingMode.HALF_UP);
            var mediaVendasArredondada = BigDecimal.valueOf(mediaVendas).setScale(2, RoundingMode.HALF_UP);

            // Montar a resposta para cada vendedor
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

