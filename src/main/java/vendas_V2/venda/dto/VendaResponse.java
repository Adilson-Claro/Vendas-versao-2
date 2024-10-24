package vendas_V2.venda.dto;

import vendas_V2.venda.model.Venda;

import java.math.BigDecimal;

public record VendaResponse(Long id,
                            Integer quantidade,
                            BigDecimal totalVendas,
                            Double valorTotal) {

    public static VendaResponse convert(Venda venda) {
        return new VendaResponse(
                venda.getId(),
                venda.getQuantidade(),
                venda.getTotalVendas(),
                venda.getValorTotalDeVendas()
        );
    }
}
