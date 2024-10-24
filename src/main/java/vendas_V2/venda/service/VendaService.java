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

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
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
                    var vendedor = validations.verificarVendedorExistente(venda.getVendedorId());
                    var produto = validations.verificarProdutoExistente(venda.getProdutoId());

                    return VendaResponseCompleta.convert(VendaResponse.convert(venda),
                            ProdutoResponse.convert(produto),
                            VendedorResponse.convert(vendedor));
                }).toList();
    }

    public void deletarVenda(Long id) {

        var venda = validations.verificarVendaExistente(id);

        vendaRepository.deleteById(id);
    }

    public VendaResponseCompleta alterarVenda(Long id, VendaRequest vendaRequest) {
        // Busca a venda pelo ID
        var vendaExistente = validations.verificarVendaExistente(id);

        // Busca o vendedor e o produto pelos IDs informados no request
        var vendedor = validations.verificarVendedorExistente(id);

        var produto = validations.verificarProdutoExistente(id);

        // Atualiza os campos da venda
        vendaExistente.setVendedorId(vendedor.getId());
        vendaExistente.setProdutoId(produto.getId());
        vendaExistente.setQuantidade(vendaRequest.quantidade());

        // Calcula o total de vendas com base no preço do produto e na quantidade
        var totalVendas = produto.getValor().multiply(new BigDecimal(vendaRequest.quantidade()));
        vendaExistente.setTotalVendas(totalVendas);

        // Salva as alterações
        var vendaAtualizada = vendaRepository.save(vendaExistente);

        // Retorna a resposta completa usando VendaResponseCompleta
        return VendaResponseCompleta.convert(
                VendaResponse.convert(vendaAtualizada),
                ProdutoResponse.convert(produto),
                VendedorResponse.convert(vendedor)
        );
    }
}

