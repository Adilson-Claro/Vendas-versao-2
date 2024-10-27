package vendas_V2.venda.dto;

import vendas_V2.venda.model.Venda;

public record VendaResponse(Long id,
                            Integer quantidade,
                            Integer totalVendas,
                            Double valorTotal,
                            Double mediaVendas
) {
    public static VendaResponse convert(Venda venda, Integer totalVendas, Double valorTotal, Double mediaVendas) {
        return new VendaResponse(
                venda.getId(),
                venda.getQuantidade(),
                totalVendas,
                valorTotal,
                mediaVendas
        );
    }
}

