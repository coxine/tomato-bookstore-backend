package tg.cos.tomatomall.po;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tg.cos.tomatomall.vo.AccountVO;

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

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "location")
    private String location;

    public AccountVO toVO() {
        AccountVO vo = new AccountVO();
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
