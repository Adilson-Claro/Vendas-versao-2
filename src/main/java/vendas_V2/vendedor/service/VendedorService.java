package vendas_V2.vendedor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.utils.NotFoundException;
import vendas_V2.vendedor.dto.VendedorRequest;
import vendas_V2.vendedor.dto.VendedorResponse;
import vendas_V2.vendedor.model.Vendedor;
import vendas_V2.vendedor.repository.VendedorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository vendedorRepository;

    public Vendedor salvarVendedor(VendedorRequest request) {

        var vendedor = construirVendedor(request.nome(), request.cpf());

        vendedorRepository.save(vendedor);
        return vendedor;
    }

    private Vendedor construirVendedor(String nome, String cpf) {

        return Vendedor.convert(null, nome, cpf);
    }

    public List<VendedorResponse> buscarVendedor() {
        var vendedor = vendedorRepository.findAll();

        if (vendedor.isEmpty()) {
            throw new NotFoundException("Vendedor não encontrado");
        }

        return vendedor.stream()
                .map(VendedorResponse::convert)
                .toList();
    }
}