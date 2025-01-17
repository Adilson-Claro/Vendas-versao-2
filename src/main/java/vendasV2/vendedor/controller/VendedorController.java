package vendasV2.vendedor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vendasV2.vendedor.dto.VendedorRequest;
import vendasV2.vendedor.dto.VendedorResponse;
import vendasV2.vendedor.service.VendedorService;

import java.util.List;

@RestController
@RequestMapping("vendedor")
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorService vendedorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> cadastrarVendedores(@RequestBody @Valid List<VendedorRequest> vendedorRequests) {
        vendedorService.salvarListaVendedores(vendedorRequests);
        return ResponseEntity.ok("Vendedores salvos com sucesso!");
    }

    @GetMapping("{id}")
    public ResponseEntity<List<VendedorResponse>> buscarVendedor(@PathVariable Long id, VendedorRequest vendedorRequest) {
        var vendedores = vendedorService.buscarVendedor(vendedorRequest);
        return ResponseEntity.ok(vendedores);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarVendedor(@PathVariable Long id) {
        vendedorService.deletarVendedor(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("reativar/{id}")
    public ResponseEntity<String> reativarVendedor(@PathVariable Long id) {
        vendedorService.reativarVendedor(id);
        return ResponseEntity.ok("Vendedor reativado");
    }

    @PutMapping("{id}")
    public ResponseEntity<String> atualizarVendedor(@PathVariable Long id, @RequestBody VendedorRequest vendedorRequest) {
        vendedorService.atualizarVendedor(id, vendedorRequest);
        return ResponseEntity.ok("Dados do vendedor atualizados com sucesso!");
    }
}
