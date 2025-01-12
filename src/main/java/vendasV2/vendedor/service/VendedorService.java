package vendasV2.vendedor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vendasV2.common.ExceptionsUtils.NotFoundException;
import vendasV2.common.ValidationsUtils.Validations;
import vendasV2.vendedor.repository.VendedorRepository;
import vendasV2.vendedor.dto.VendedorRequest;
import vendasV2.vendedor.dto.VendedorResponse;
import vendasV2.vendedor.model.Vendedor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final Validations validations;

    public void salvarListaVendedores(List<VendedorRequest> vendedores) {
        if (vendedores.isEmpty()) {
            throw new IllegalArgumentException("A lista de vendedores não pode ser vazia");
        }

        List<Vendedor> listaVendedores = new ArrayList<>();

        for (VendedorRequest vendedorRequest : vendedores) {
            validarVendedor(vendedorRequest);

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

    private void validarVendedor(VendedorRequest vendedorRequest) {
        if (vendedorRequest.nome() == null || vendedorRequest.nome().isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        if (vendedorRequest.cpf() == null || vendedorRequest.cpf().isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
        }

        if (!isCpfValido(vendedorRequest.cpf())) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    private boolean isCpfValido(String cpf) {

        return cpf != null && cpf.matches("\\d{11}");
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

    public void deletarVendedor(Long vendedorId) {
        Vendedor vendedor = vendedorRepository.findById(vendedorId)
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

    public VendedorResponse atualizarVendedor(Long id, VendedorRequest request) {
        validations.verficarStatusVendedorAtivo(id);

        var existVendedor = validations.verificarVendedorExistente(id);

        if (existVendedor == null) {
            throw new NotFoundException("Vendedor não encontrado.");
        }

        existVendedor.setNome(request.nome());
        existVendedor.setCpf(request.cpf());

        var vendedorAtualizado = vendedorRepository.save(existVendedor);

        return VendedorResponse.convert(vendedorAtualizado);
    }
}
