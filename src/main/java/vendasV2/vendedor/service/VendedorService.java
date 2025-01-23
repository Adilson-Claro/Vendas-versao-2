package vendasV2.vendedor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendasV2.common.ExceptionsUtils.NotFoundException;
import vendasV2.common.ValidationsUtils.Validations;
import vendasV2.vendedor.dto.VendedorRequest;
import vendasV2.vendedor.dto.VendedorResponse;
import vendasV2.vendedor.model.Vendedor;
import vendasV2.vendedor.repository.VendedorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final Validations validations;

    public void salvarListaVendedores(List<VendedorRequest> vendedores) {

        validations.validarVendedoresJaCadastrados(vendedores);
        validations.validarVendedor(vendedores);

        List<Vendedor> listaVendedores = vendedores.stream()
                .map(vendedorRequest -> Vendedor.to(
                        null,
                        vendedorRequest.nome(),
                        vendedorRequest.cpf()
                )).toList();

        vendedorRepository.saveAll(listaVendedores);

    }

    public List<VendedorResponse> buscarVendedor(VendedorRequest request) {
        var vendedor = vendedorRepository.findById(request.id());

        return vendedor.stream()
                .map(VendedorResponse::convert)
                .toList();
    }

    public void deletarVendedor(Long vendedorId) {
        var vendedor = vendedorRepository.findById(vendedorId)
                .orElseThrow(() -> new NotFoundException("Vendedor não encontrado"));

        if (vendedor.getStatus() == Vendedor.statusVendedor.INATIVO) {
            throw new IllegalStateException("Vendedor já está inativo");
        }

        vendedor.setStatus(Vendedor.statusVendedor.INATIVO);
        vendedorRepository.save(vendedor);
    }

    public void reativarVendedor(Long id) {
        var vendedorStatus = validations.verficarStatusVendedorInativo(id);

        if (vendedorStatus == null) {
            throw new IllegalArgumentException("Vendedor já está ativo");
        }

        var vendedor = validations.verificarVendedorExistente(id);

        vendedorStatus.setStatus(Vendedor.statusVendedor.ATIVO);
        vendedorRepository.save(vendedor);
    }

    public void atualizarVendedor(Long id, VendedorRequest request) {
        validations.verficarStatusVendedorAtivo(id);

        var vendedorExistente = validations.verificarVendedorExistente(id);

        vendedorExistente.setNome(request.nome());
        vendedorExistente.setCpf(request.cpf());

        var vendedorAtualizado = vendedorRepository.save(vendedorExistente);

        VendedorResponse.convert(vendedorAtualizado);
    }
}
