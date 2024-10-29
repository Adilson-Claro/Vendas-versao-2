package vendas_V2.vendedor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.utils.NotFoundException;
import vendas_V2.common.utils.validations.Validations;
import vendas_V2.vendedor.dto.VendedorRequest;
import vendas_V2.vendedor.dto.VendedorResponse;
import vendas_V2.vendedor.model.Vendedor;
import vendas_V2.vendedor.repository.VendedorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final Validations validations;

    public List<Vendedor> salvarListaVendedores(List<VendedorRequest> vendedor) {
        var listaVendedores = vendedor.stream()
                .map(vendedorRequest -> new Vendedor(null, vendedorRequest.nome(), vendedorRequest.cpf()))
                .collect(Collectors.toList());
        return vendedorRepository.saveAll(listaVendedores);
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
    public void deletarVendedor(Long id) {

        var vendedor = validations.verificarVendedorExistente(id);

        vendedorRepository.deleteById(id);
    }

    public VendedorResponse atualizarVendedor(Long id, VendedorRequest request) {

        var existVendedor = validations.verificarVendedorExistente(id);

        existVendedor.setNome(request.nome());
        existVendedor.setCpf(request.cpf());

        var vendedorAtualizado = vendedorRepository.save(existVendedor);

        return VendedorResponse.convert(vendedorAtualizado);
    }
}
