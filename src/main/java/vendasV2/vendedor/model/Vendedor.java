package vendasV2.vendedor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cpf;

    @Enumerated(EnumType.STRING)
    private statusVendedor status = statusVendedor.ATIVO;

    public static Vendedor convert(Long id, String nome, String cpf) {
        var vendedor = new Vendedor();
        vendedor.setId(id);
        vendedor.setNome(nome);
        vendedor.setCpf(cpf);
        vendedor.setStatus(statusVendedor.ATIVO);
        return vendedor;
    }

    public enum statusVendedor {
        ATIVO,
        INATIVO
    }
}
