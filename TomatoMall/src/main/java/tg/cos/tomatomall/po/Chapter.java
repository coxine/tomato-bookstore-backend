package tg.cos.tomatomall.po;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "chapter")
@Getter
@Setter
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "previous")
    private Integer previous;
    @Column(name = "next")
    private Integer next;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "price")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PRODUCT_CHAPTER"))
    private Product product;

    @ManyToMany
    private List<Account> accounts;

}
