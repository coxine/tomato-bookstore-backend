package tg.cos.tomatomall.po;

import jakarta.persistence.*;
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

    @Column(name = "item", nullable = false)
    private String item;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "product_id", nullable = false)
    private Integer productId;


}