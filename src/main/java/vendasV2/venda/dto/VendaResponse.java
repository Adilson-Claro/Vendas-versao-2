package vendasV2.venda.dto;

import vendasV2.venda.model.Venda;

public record VendaResponse(Long id,
                            Integer quantidade,
                            Integer totalVendas,
                            Double valorTotal,
                            Double mediaVendas,
                            Venda.statusVenda statusVenda

) {

    public static VendaResponse convert(Venda venda, Integer totalVendas, Double valorTotal, Double mediaVendas) {
        return new VendaResponse(
                venda.getId(),
                venda.getQuantidade(),
                totalVendas,
                valorTotal,
                mediaVendas,
                venda.getStatus()
        );
    }
}

