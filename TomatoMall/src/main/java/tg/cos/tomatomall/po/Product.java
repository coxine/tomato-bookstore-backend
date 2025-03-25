package tg.cos.tomatomall.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "rate", nullable = false)
    private Float rate;

    @Column(name = "description")
    private String description;
    @Column(name = "cover")
    private String cover;
    @Column(name = "detail")
    private String detail;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Specification> specifications;

//    @Column(name = "stockpile")
//    @JsonIgnore
//    private Stockpile stockpile;


}