package vendasV2.vendedor.dto;

import vendasV2.vendedor.model.Vendedor;

public record VendedorResponse(Long id,
                               String nome,
                               String cpf,
                               Vendedor.statusVendedor status) {

    public static VendedorResponse convert(Vendedor vendedor) {
        return new VendedorResponse(vendedor.getId(),
                vendedor.getNome(),
                vendedor.getCpf(),
                vendedor.getStatus());
    }
}
