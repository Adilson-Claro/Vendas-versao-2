package vendas_V2.produto.model;

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

    public static Produto convert(Long id, String nome, BigDecimal valor) {
        var produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setValor(valor);
        return produto;
    }
}
