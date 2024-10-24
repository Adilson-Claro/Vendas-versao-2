package vendas_V2.common.utils.validations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vendas_V2.common.utils.NotFoundException;
import vendas_V2.produto.model.Produto;
import vendas_V2.produto.repository.ProdutoRepository;
import vendas_V2.vendedor.model.Vendedor;
import vendas_V2.vendedor.repository.VendedorRepository;

@Component
@RequiredArgsConstructor
public class Validations {

    private final ProdutoRepository produtoRepository;
    private final VendedorRepository vendedorRepository;

    public Produto verificarProdutoExistente(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado."));
    }

    public Vendedor verificarVendedorExistente(Long id) {
        return vendedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vendedor não encontrado."));
    }
}
