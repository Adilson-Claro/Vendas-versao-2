package vendasV2.produto.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private Integer quantidade;

    public static Produto to(Long id, String nome, BigDecimal valor, Integer quantidade) {
        var produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setValor(valor);
        produto.setQuantidade(quantidade);
        return produto;
    }
}
