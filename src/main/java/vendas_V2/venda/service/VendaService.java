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
import vendas_V2.venda.dto.VendaResponseMedia;
import vendas_V2.venda.model.Venda;
import vendas_V2.venda.repository.VendaRepository;
import vendas_V2.vendedor.dto.VendedorResponse;
import vendas_V2.vendedor.model.Vendedor;
import vendas_V2.vendedor.repository.VendedorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendedorRepository vendedorRepository;
    private final ProdutoRepository produtoRepository;
    private final Calculos calculos;
    private final Validations validations;

    public void salvarVenda(VendaRequest request) {

        var produto = validations.verificarProdutoExistente(request.produtoId());

        var vendedor = validations.verificarVendedorExistente(request.vendedorId());

        var totalVenda = calculos.calcularTotalVenda(produto.getValor(), request.quantidade());

        var venda = calculos.construirVenda(vendedor.getId(), produto.getId(), request.quantidade(), totalVenda);

        vendaRepository.save(venda);
    }

    public List<VendaResponseCompleta> buscarVendas() {
        var vendas = vendaRepository.findAll();

        return vendas
                .stream()
                .map(venda -> {
                    var vendedor = VendedorResponse.convert(vendedorRepository.findById(venda.getVendedorId())
                            .orElseThrow(() -> new NotFoundException("Vendedor não encontrado.")));
                    var produto = ProdutoResponse.convert(produtoRepository.findById(venda.getProdutoId())
                            .orElseThrow(() -> new NotFoundException("Produto não encontrado.")));

                    return VendaResponseCompleta.convert(VendaResponse.convert(venda),
                            produto,
                            vendedor);
                }).toList();
    }

    public List<VendaResponseMedia> calcularMediaDeVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Long produtoId) {
        List<VendaResponseMedia> medias = new ArrayList<>();
        List<Vendedor> vendedores = vendedorRepository.findAll();

        // Converte as datas de LocalDate para LocalDateTime para a consulta
        LocalDate startDateTime = LocalDate.from(dataInicio.atStartOfDay());
        LocalDate endDateTime = LocalDate.from(dataFim.atTime(23, 59, 59));

        for (Vendedor vendedor : vendedores) {
            // Filtra as vendas do vendedor no período especificado
            List<Venda> vendasDoVendedor = vendaRepository.findByVendedorIdAndDataCadastroBetween(
                    vendedor.getId(), dataInicio, dataFim
            );

            // Verifica se existem vendas no período
            if (vendasDoVendedor.isEmpty()) {
                continue; // Se não houver vendas, pula para o próximo vendedor
            }

            // Calcula a média de vendas por vendedor
            BigDecimal media = calculos.calcularMediaDeVendasPorVendedor(vendedor.getId(), vendasDoVendedor, produtoId);

            // Calcula o total de vendas para o vendedor
            BigDecimal totalVendas = calculos.calcularTotalVendas(vendasDoVendedor, produtoId);

            // Calcula a quantidade total de vendas
            Integer quantidadeTotal = calculos.calcularQuantidadeTotal(vendasDoVendedor);

            // Criação da resposta com as datas formatadas e outros dados
            VendaResponseMedia vendaResponseMedia = new VendaResponseMedia(
                    dataInicio,
                    dataFim,
                    media,
                    vendedor.getId(),
                    vendedor.getNome(),
                    quantidadeTotal // Ou outra variável que represente o total de vendas
            );

            medias.add(vendaResponseMedia);
        }

        return medias; // Retorna a lista de médias
    }
}

