package tg.cos.tomatomall.po;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stockpile")
public class Stockpile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "frozen", nullable = false)
    private Integer frozen;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}