package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tg.cos.tomatomall.po.Account;

@Getter
@Setter
@NoArgsConstructor
public class AccountVO {
    private Integer id;
    private String username;
    private String name;
    private String role;
    private String password;
    private String avatar;
    private String telephone;
    private String email;
    private String location;

    public Account toAccount() {
        Account account = new Account();
        if (id != null) {
            account.setId(id);
        }
        account.setUsername(username);
        account.setName(name);
        account.setPassword(password);
        account.setAvatar(role);
        if (avatar != null) {
            account.setAvatar(avatar);
        }
        if (telephone != null) {
            account.setTelephone(telephone);
        }
        if (email != null) {
            account.setEmail(email);
        }
        if (location != null) {
            account.setLocation(location);
        }
        return account;
    }
}
