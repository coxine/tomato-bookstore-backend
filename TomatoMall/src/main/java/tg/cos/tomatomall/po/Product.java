package tg.cos.tomatomall.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.dto.SpecificationDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

    @Column(name = "title", nullable = false, length = 50)
    @Size(max = 50)
    private String title;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "0", inclusive = true, message = "价格不能为负数")
    @DecimalMax(value = "9999999.99", message = "价格不能超过9,999,999.99")
    @Digits(integer = 7, fraction = 2, message = "价格格式应为：最多7位整数，2位小数")
    private BigDecimal price;

    @Column(name = "rate", nullable = false)
    @Min(value = 0)
    @Max(value = 10)
    private Float rate;

    @Column(name = "description", length = 255)
    @Size(max = 255)
    private String description;
    @Column(name = "cover", length = 500)
    @Size(max = 500)
    private String cover;
    @Column(name = "last_change_cover")
    private Date lastChangeCover;
    @Column(name = "detail", length = 500)
    @Size(max = 500)
    private String detail;
    @Column(name = "ratePeopleNumber")
    private Integer ratePeopleNumber;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Specification> specifications;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stockpile stockpile;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Advertisement> advertisements;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chapter> chapters;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_tags",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

//    }
}