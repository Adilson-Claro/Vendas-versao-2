package vendas_V2.venda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long vendedorId;

    @Column(nullable = false)
    private Long produtoId;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal totalVendas;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private Double valorTotalDeVendas;

    public static Venda convert(Long vendedorId, Long produtoId, Integer quantidade, BigDecimal totalVendas) {
        var venda = new Venda();
        venda.setVendedorId(vendedorId);
        venda.setProdutoId(produtoId);
        venda.setQuantidade(quantidade);
        venda.setTotalVendas(totalVendas);
        venda.setDataCadastro(LocalDateTime.now());
        venda.setValorTotalDeVendas(totalVendas.doubleValue() * quantidade);
        return venda;
    }

}

