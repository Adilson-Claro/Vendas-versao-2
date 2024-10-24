package vendas_V2.venda.dto;

import jakarta.validation.constraints.NotNull;

public record VendaRequest(@NotNull Long vendedorId,
                           @NotNull Long produtoId,
                           @NotNull Integer quantidade) {
}
