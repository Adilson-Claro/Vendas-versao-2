package vendas_V2.vendedor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vendas_V2.vendedor.dto.VendedorRequest;
import vendas_V2.vendedor.dto.VendedorResponse;
import vendas_V2.vendedor.service.VendedorService;

import java.util.List;

@RestController
@RequestMapping("vendedor")
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorService vendedorService;

    @PostMapping
    public ResponseEntity<String> cadastrarVendedores(@RequestBody @Valid List<VendedorRequest> request) {
        vendedorService.salvarListaVendedores(request);
        return ResponseEntity.ok("Vendedores cadastrados com sucesso!");
    }

    @GetMapping
    public ResponseEntity<List<VendedorResponse>> buscarVendedor() {
        var vendedores = vendedorService.buscarVendedor();
        return ResponseEntity.ok(vendedores);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarVendedor(@PathVariable Long id) {
        vendedorService.deletarVendedor(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/reativar")
    public ResponseEntity<String> reativarVendedor(@PathVariable Long id) {
        vendedorService.reativarVendedor(id);
        return ResponseEntity.ok("Vendedor reativado");
    }

    @PutMapping("{id}")
    public ResponseEntity<String> atualizarVendedor(
            @PathVariable Long id,
            @RequestBody VendedorRequest request) {

        vendedorService.atualizarVendedor(id, request);

        return ResponseEntity.ok("Dados do vendedor atualizados com sucesso!");
    }
}
