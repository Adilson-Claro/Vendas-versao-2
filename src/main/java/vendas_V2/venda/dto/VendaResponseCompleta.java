package vendas_V2.venda.dto;

import vendas_V2.produto.dto.ProdutoResponse;
import vendas_V2.vendedor.dto.VendedorResponse;

public record VendaResponseCompleta(VendaResponse venda,
                                    ProdutoResponse produto,
                                    VendedorResponse vendedor) {

    public static VendaResponseCompleta convert(VendaResponse venda, ProdutoResponse produto, VendedorResponse vendedor) {
        return new VendaResponseCompleta(venda,
                produto,
                vendedor);
    }
}
