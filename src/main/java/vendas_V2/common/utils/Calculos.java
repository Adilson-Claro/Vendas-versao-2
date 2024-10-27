package vendas_V2.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vendas_V2.common.utils.validations.Validations;
import vendas_V2.venda.model.Venda;
import vendas_V2.venda.repository.VendaRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Calculos {

    private final VendaRepository vendaRepository;

    public Integer calcularTotalVendasPorVendedor(Long vendedorId) {
        var vendasDoVendedor = vendaRepository.findByVendedorId(vendedorId);
        return vendasDoVendedor.size();
    }

    public Venda construirVenda(Long vendedorId, Long produtoId, Integer quantidade) {
        return Venda.convert(vendedorId, produtoId, quantidade);
    }

    public Double calcularValorTotal(Integer quantidade, BigDecimal valorUnitario) {
        return BigDecimal.valueOf(quantidade)
                .multiply(valorUnitario)
                .doubleValue();
    }
}
