package vendas_V2.venda.dto;

import vendas_V2.venda.model.Venda;

import java.math.BigDecimal;
import java.time.LocalDate;

import static vendas_V2.common.utils.Data.formatarData;

public record VendaResponse(Long id,
                            Integer quantidade,
                            BigDecimal totalVendas,
                            Double valorTotal,
                            String dataCadastro) {

    public static VendaResponse convert(Venda venda) {
        return new VendaResponse(
                venda.getId(),
                venda.getQuantidade(),
                venda.getTotalVendas(),
                venda.getValorTotalDeVendas(),
                formatarData(LocalDate.from(venda.getDataCadastro()))
        );
    }
}
