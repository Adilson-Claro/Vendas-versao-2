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
    public ResponseEntity<VendedorResponse> salvarVendedor(@RequestBody @Valid VendedorRequest request) {
        var vendedorCriado = VendedorResponse.convert(vendedorService.salvarVendedor(request));

        return ResponseEntity.ok(vendedorCriado);
    }

    @GetMapping
    public ResponseEntity<List<VendedorResponse>> buscarVendedor() {
        var vendedores = vendedorService.buscarVendedor();
        return ResponseEntity.ok(vendedores);
    }
}
