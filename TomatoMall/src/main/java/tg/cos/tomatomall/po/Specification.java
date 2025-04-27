package tg.cos.tomatomall.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "specifications")
public class Specification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "item", nullable = false,length = 50)
    @Size(max = 50)
    private String item;

    @Column(name = "value", nullable = false, length = 255)
    @Size(max = 255)
    private String value;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "FK_PROCUDT_SPECIFICATION"))
    private Product product;


}