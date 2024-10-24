package vendas_V2.produto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequest(Long id,
                             @NotBlank String nome,
                             @NotNull BigDecimal valor) {
}
