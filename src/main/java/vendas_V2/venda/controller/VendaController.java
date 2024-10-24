package vendas_V2.venda.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vendas_V2.venda.dto.VendaRequest;
import vendas_V2.venda.dto.VendaResponseCompleta;
import vendas_V2.venda.service.VendaService;

import java.util.List;

@RestController
@RequestMapping("venda")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaRequest> salvarVenda(@RequestBody @Valid VendaRequest request) {
        vendaService.salvarVenda(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VendaResponseCompleta>> buscarVenda() {
        var vendas = vendaService.buscarVendas();
        return ResponseEntity.ok(vendas);
    }

}




