package vendasV2.venda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vendasV2.produto.model.Produto;
import vendasV2.vendedor.model.Vendedor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private statusVenda status = statusVenda.ANDAMENTO;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    public static Venda convert(Vendedor vendedor, Produto produto, Integer quantidade, BigDecimal valorTotal) {
        var venda = new Venda();
        venda.setVendedor(vendedor);
        venda.setProduto(produto);
        venda.setQuantidade(quantidade);
        venda.setStatus(statusVenda.ANDAMENTO);
        venda.setDataCadastro(LocalDateTime.now());
        venda.setValorTotal(valorTotal);
        return venda;
    }

    public enum statusVenda {
        APROVADO,
        CANCELADO,
        ANDAMENTO
    }
}


