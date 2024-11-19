package vendas_V2.vendedor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendas_V2.common.ExceptionsUtils.NotFoundException;
import vendas_V2.common.ValidationsUtils.Validations;
import vendas_V2.vendedor.dto.VendedorRequest;
import vendas_V2.vendedor.dto.VendedorResponse;
import vendas_V2.vendedor.model.Vendedor;
import vendas_V2.vendedor.repository.VendedorRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final Validations validations;

    public void salvarListaVendedores(List<VendedorRequest> vendedores) {
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

        vendedorRepository.saveAll(listaVendedores);
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

        validations.verficarStatusVendedorAtivo(id);
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

        validations.verficarStatusVendedorAtivo(id);
        var existVendedor = validations.verificarVendedorExistente(id);

        existVendedor.setNome(request.nome());
        existVendedor.setCpf(request.cpf());

        var vendedorAtualizado = vendedorRepository.save(existVendedor);

        return VendedorResponse.convert(vendedorAtualizado);
    }
}
