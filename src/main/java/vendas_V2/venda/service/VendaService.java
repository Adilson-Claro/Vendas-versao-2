package vendas_V2.venda.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.ExceptionsUtils.NotFoundException;
import vendas_V2.common.ValidationsUtils.Validations;
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
    private final Validations validations;
    private final ProdutoRepository produtoRepository;

    public void salvarVenda(VendaRequest request) {
        var produto = validations.verificarProdutoExistente(request.produtoId());
        var vendedor = validations.verificarVendedorExistente(request.vendedorId());
        validations.verficarStatusVendedorAtivo(request.vendedorId());
        validations.verificarQuantidadeEstoque(produto, request.quantidade());

        var valorTotal = produto.getValor().multiply(new BigDecimal(request.quantidade()));
        var venda = Venda.convert(vendedor, produto, request.quantidade(), valorTotal);
        vendaRepository.save(venda);
    }

    public Venda buscarVendaPorId(Long vendaId) {

        return validations.buscarVendaPorId(vendaId);
    }

    @Transactional
    public Venda aprovarVenda(Long vendaId) {
        var venda = buscarVendaPorId(vendaId);

        var produto = validations.verificarProdutoExistente(venda.getProduto().getId());
        produto.setQuantidade(produto.getQuantidade() - venda.getQuantidade());
        produtoRepository.save(produto);

        venda.setStatus(Venda.statusVenda.APROVADO);
        return vendaRepository.save(venda);
    }

    @Transactional
    public void cancelarVenda(Long vendaId) {
        var optionalVenda = vendaRepository.findById(vendaId);
        if (optionalVenda.isPresent()) {
            var venda = optionalVenda.get();
            var produto = validations.verificarProdutoExistente(venda.getProduto().getId());

            if (venda.getStatus() == Venda.statusVenda.APROVADO || venda.getStatus() == Venda.statusVenda.ANDAMENTO) {
                produto.setQuantidade(produto.getQuantidade() + venda.getQuantidade());
                produtoRepository.save(produto);
            }

            venda.setStatus(Venda.statusVenda.CANCELADO);
            vendaRepository.save(venda);
        }
    }

    public List<VendaResponseCompleta> buscarVendasPorVendedor(Long vendedorId) {
        var vendas = vendaRepository.findByVendedorId(vendedorId);

        return vendas.stream()
                .map(venda -> {
                    var vendedor = validations.verificarVendedorExistente(venda.getVendedor().getId());
                    var produto = validations.verificarProdutoExistente(venda.getProduto().getId());
                    var vendaExistente = validations.verificarVendaExistente(venda.getId());

                    var totalVendas = vendaRepository.calcularTotalVendasPorVendedor(vendedorId);
                    var valorTotal = vendaRepository.calcularValorTotal(venda.getQuantidade(), produto.getId());
                    var mediaVendas = vendaRepository.calcularMediaVendas(vendedorId);

                    return VendaResponseCompleta.convert(
                            VendaResponse.convert(vendaExistente, totalVendas, valorTotal, mediaVendas),
                            ProdutoResponse.convert(produto),
                            VendedorResponse.convert(vendedor)
                    );
                }).collect(Collectors.toList());
    }

    @Transactional
    public void alterarVenda(Long id, VendaRequest vendaRequest) {

        var vendaExistente = validations.verificarVendaExistente(id);
        var vendedor = validations.verificarVendedorExistente(vendaExistente.getVendedor().getId());
        var produto = validations.verificarProdutoExistente(vendaExistente.getProduto().getId());

        vendaExistente.setQuantidade(vendaRequest.quantidade());
        var vendaAtualizada = vendaRepository.save(vendaExistente);

        var totalVendas = vendaRepository.calcularTotalVendasPorVendedor(vendaRequest.vendedorId());
        var valorTotal = vendaRepository.calcularValorTotal(vendaRequest.quantidade(), vendaRequest.produtoId());
        var mediaVendas = vendaRepository.calcularMediaVendas(vendaRequest.vendedorId());

        VendaResponseCompleta.convert(
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
                    var produto = validations.verificarProdutoExistente(venda.getProduto().getId());
                    return vendaRepository.calcularValorTotal(venda.getQuantidade(), produto.getId());
                })
                .sum();


        var diasNoPeriodo = ChronoUnit.DAYS.between(request.getDataInicio(), request.getDataFim()) + 1;
        var mediaVendas = diasNoPeriodo > 0 ? valorTotal / diasNoPeriodo : 0;

        var valorTotalArredondado = BigDecimal.valueOf(valorTotal).setScale(2, RoundingMode.HALF_UP);
        var mediaVendasArredondada = BigDecimal.valueOf(mediaVendas).setScale(2, RoundingMode.HALF_UP);

        return vendas.stream()
                .map(venda -> {
                    var produto = validations.verificarProdutoExistente(venda.getProduto().getId());
                    return VendaResponseCompleta.convert(
                            VendaResponse.convert(venda, vendas.size(), valorTotalArredondado.doubleValue(), mediaVendasArredondada.doubleValue()),
                            ProdutoResponse.convert(produto),
                            VendedorResponse.convert(vendedor)
                    );
                })
                .collect(Collectors.toList());
    }
}

