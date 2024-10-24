package vendas_V2.venda.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class VendaResponseMedia {

    private String dataInicio;
    private String dataFim;
    private BigDecimal mediaVendasDiaria;
    private Long vendedorId;
    private String vendedorNome;
    private Integer totalVendas;

    public VendaResponseMedia(LocalDate dataInicio, LocalDate dataFim,
                              BigDecimal mediaVendasPorDia, Long vendedorId, String vendedorNome, Integer totalVendas) {
        this.dataInicio = formatarData(dataInicio);
        this.dataFim = formatarData(dataFim);
        this.mediaVendasDiaria = mediaVendasPorDia;
        this.vendedorId = vendedorId;
        this.vendedorNome = vendedorNome;
        this.totalVendas = totalVendas;
    }

    private String formatarData(LocalDate data) {
        if (data != null) {
            var dataFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return data.format(dataFormat);
        }
        return null;
    }
}
