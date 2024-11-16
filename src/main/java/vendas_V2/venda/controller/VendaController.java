package vendas_V2.venda.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vendas_V2.common.utils.NotFoundException;
import vendas_V2.venda.dto.VendaRequest;
import vendas_V2.venda.dto.VendaResponseCompleta;
import vendas_V2.venda.dto.VendasPorPeriodoRequest;
import vendas_V2.venda.model.Venda;
import vendas_V2.venda.repository.VendaRepository;
import vendas_V2.venda.service.VendaService;

import java.util.List;

@RestController
@RequestMapping("venda")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;
    private final VendaRepository vendaRepository;

    @PostMapping
    public ResponseEntity<VendaRequest> criarVenda(@RequestBody @Valid VendaRequest request) {
        vendaService.salvarVenda(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("aprovar/{id}")
    public ResponseEntity<Venda> aprovarVenda(@PathVariable Long id) {
        Venda venda = vendaService.aprovarVenda(id);
        return ResponseEntity.ok(venda);
    }

    @PutMapping("cancelar/{id}")
    public ResponseEntity<String> cancelarVenda(@PathVariable Long id) {
        try {
            vendaService.cancelarVenda(id);
            return ResponseEntity.ok("Venda cancelada com sucesso!");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Venda não encontrada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cancelar a venda.");
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<List<VendaResponseCompleta>> buscarVendaPorVendedor(@PathVariable Long id) {
        var vendas = vendaService.buscarVendasPorVendedor(id);
        return ResponseEntity.ok(vendas);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> alterarVenda(
            @PathVariable Long id,
            @RequestBody VendaRequest vendaRequest) {

        var vendaAtualizada = vendaService.alterarVenda(id, vendaRequest);

        return ResponseEntity.ok("Venda atualizada com sucesso!");
    }

    @GetMapping("{id}/periodo")
    public List<VendaResponseCompleta> buscarVendasPorPeriodo(
            @PathVariable("id") Long idVendedor,
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {

        var request = new VendasPorPeriodoRequest(idVendedor, dataInicio, dataFim);
        return vendaService.buscarVendasPorPeriodo(idVendedor, request);
    }
}




