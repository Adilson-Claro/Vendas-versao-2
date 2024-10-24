package vendas_V2.venda.dto;

import vendas_V2.venda.model.Venda;

import java.math.BigDecimal;

public record VendaResponse(Long id,
                            Integer quantidade,
                            BigDecimal totalVendas
) {

    public static VendaResponse convert(Venda venda) {
        return new VendaResponse(
                venda.getId(),
                venda.getQuantidade(),
                venda.getTotalVendas()
        );
    }
}
