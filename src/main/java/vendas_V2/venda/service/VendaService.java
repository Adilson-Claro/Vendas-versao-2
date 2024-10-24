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
import vendas_V2.venda.repository.VendaRepository;
import vendas_V2.vendedor.dto.VendedorResponse;
import vendas_V2.vendedor.repository.VendedorRepository;

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
}

