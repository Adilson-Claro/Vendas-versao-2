package vendasV2.venda.dto;

import vendasV2.produto.dto.ProdutoResponse;
import vendasV2.vendedor.dto.VendedorResponse;

public record VendaResponseCompleta(VendaResponse venda,
                                    ProdutoResponse produto,
                                    VendedorResponse vendedor) {

    public static VendaResponseCompleta to(VendaResponse venda,
                                           ProdutoResponse produto,
                                           VendedorResponse vendedor) {
        return new VendaResponseCompleta(venda,
                produto,
                vendedor);
    }
}
