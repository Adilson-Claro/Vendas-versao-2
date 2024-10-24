package vendas_V2.venda.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vendas_V2.venda.dto.VendaRequest;
import vendas_V2.venda.dto.VendaResponseCompleta;
import vendas_V2.venda.dto.VendaResponseMedia;
import vendas_V2.venda.service.VendaService;

import java.time.LocalDate;
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

    @GetMapping("media")
    public ResponseEntity<List<VendaResponseMedia>> calcularMediaDeVendasPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam Long produtoId) {

        List<VendaResponseMedia> medias = vendaService.calcularMediaDeVendasPorPeriodo(dataInicio, dataFim, produtoId);

        if (medias.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content se não houver dados
        }

        return ResponseEntity.ok(medias); // Retorna 200 OK com a lista de médias
    }
}




