package vendas_V2.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vendas_V2.common.utils.validations.Validations;
import vendas_V2.venda.model.Venda;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Calculos {

    private final Validations validations;

    public Integer totalVendas(List<Venda> vendas) {
        return vendas.size();
    }

    public Venda construirVenda(Long vendedorId, Long produtoId, Integer quantidade) {
        return Venda.convert(vendedorId, produtoId, quantidade);
    }
}
