package vendas_V2.vendedor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.utils.NotFoundException;
import vendas_V2.common.utils.validations.Validations;
import vendas_V2.vendedor.dto.VendedorRequest;
import vendas_V2.vendedor.dto.VendedorResponse;
import vendas_V2.vendedor.model.Vendedor;
import vendas_V2.vendedor.repository.VendedorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final Validations validations;

    public List<Vendedor> salvarListaVendedores(List<VendedorRequest> vendedores) {
        List<Vendedor> listaVendedores = new ArrayList<>();

        for (VendedorRequest vendedorRequest : vendedores) {

            if (vendedorRepository.findByCpf(vendedorRequest.cpf()).isPresent()) {
                throw new NotFoundException("Vendedor com CPF " + vendedorRequest.cpf() + " já está cadastrado");
            }

            var vendedor = new Vendedor(
                    null,
                    vendedorRequest.nome(),
                    vendedorRequest.cpf(),
                    Vendedor.statusVendedor.ATIVO
            );
            listaVendedores.add(vendedor);
        }

        return vendedorRepository.saveAll(listaVendedores);
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

        var vendedorStatus = validations.verficarStatusVendedorAtivo(id);
        var vendedor = validations.verificarVendedorExistente(id);

            vendedor.setStatus(Vendedor.statusVendedor.INATIVO);
            vendedorRepository.save(vendedor);
    }

    public void reativarVendedor(Long id) {
        var vendedorStatus = validations.verficarStatusVendedorInativo(id);
        var vendedor = validations.verificarVendedorExistente(id);

        vendedorStatus.setStatus(Vendedor.statusVendedor.ATIVO);
        vendedorRepository.save(vendedor);
    }

    public VendedorResponse atualizarVendedor(Long id, VendedorRequest request) {

        var vendedorStatus = validations.verficarStatusVendedorAtivo(id);
        var existVendedor = validations.verificarVendedorExistente(id);

        existVendedor.setNome(request.nome());
        existVendedor.setCpf(request.cpf());

        var vendedorAtualizado = vendedorRepository.save(existVendedor);

        return VendedorResponse.convert(vendedorAtualizado);
    }
}
