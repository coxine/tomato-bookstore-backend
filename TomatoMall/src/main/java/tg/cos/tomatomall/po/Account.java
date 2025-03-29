package tg.cos.tomatomall.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tg.cos.tomatomall.dto.AccountDTO;

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
