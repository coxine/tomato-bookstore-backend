package tg.cos.tomatomall.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tg.cos.tomatomall.dto.AccountDTO;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private int id;

    @Column(name = "username", nullable = false, length = 50)
    @Size(max = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    @Size(max = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    @Size(max = 50)
    private String name;

    @Column(name = "role", length = 50)
    @Size(max = 50)
    private String role;

    @Column(name = "avatar", length = 255)
    @Size(max = 255)
    private String avatar;

    @Column(name = "telephone", length = 11)
    @Size(max = 11)
    private String telephone;

    @Column(name = "email", length = 100)
    @Size(max = 100)
    private String email;

    @Column(name = "location", length = 255)
    @Size(max = 255)
    private String location;

    @Basic
    @Column(name = "latest_avatar_change_time")
    private Date latestAvatarChangeTime;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Order> orders;

    @ElementCollection  // 告诉 JPA 这是一个基本类型的集合
    @CollectionTable(
            name = "product_cover_creates",  // 存储集合的中间表名
            joinColumns = @JoinColumn(name = "product_id")  // 关联主表的列
    )
    @Column(name = "cover_create_id")  // 集合元素存储的列名
    private List<Long> uploadProductCoverCreates;

    @ElementCollection  // 告诉 JPA 这是一个基本类型的集合
    @CollectionTable(
            name = "advertisement_image_creates",  // 存储集合的中间表名
            joinColumns = @JoinColumn(name = "advertisement_id")  // 关联主表的列
    )
    @Column(name = "advertisement_cover_create_id")  // 集合元素存储的列名
    private List<Long> uploadAdvertisementCoverCreates;

    @ElementCollection  // 告诉 JPA 这是一个基本类型的集合
    @CollectionTable(
            name = "rate_products",  // 存储集合的中间表名
            joinColumns = @JoinColumn(name = "product_id")  // 关联主表的列
    )
    @Column(name = "rateProduct")
    private Set<Integer> rateProducts;

    public AccountDTO toDTO() {
        AccountDTO vo = new AccountDTO();
        vo.setId(id);
        vo.setUsername(username);
        vo.setPassword(password);
        vo.setName(name);
        vo.setRole(role);
        vo.setAvatar(avatar);
        vo.setTelephone(telephone);
        vo.setEmail(email);
        vo.setLocation(location);
        return vo;
    }
}
