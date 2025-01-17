package vendasV2.vendedor.dto;

import jakarta.validation.constraints.NotBlank;

public record VendedorRequest(Long id,
                              @NotBlank String nome,
                              @NotBlank String cpf) {
}
