package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.vo.AccountGetDetailsVO;

@Getter
@Setter
@NoArgsConstructor
public class AccountDTO {
    private Integer id;
    private String username;
    private String name;
    private String role;
    private String password;
    private String newPassword;//for change password
    private String avatar;
    private String telephone;
    private String email;
    private String location;

    public Account toPO() {
        Account account = new Account();
        if (id != null) {
            account.setId(id);
        }
        account.setUsername(username);
        account.setName(name);
        account.setPassword(password);
        account.setRole(role);
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

    public AccountGetDetailsVO toGetDetailsVO() {
        AccountGetDetailsVO accountGetDetailsVO = new AccountGetDetailsVO();
        accountGetDetailsVO.setUsername(username);
        accountGetDetailsVO.setName(name);
        accountGetDetailsVO.setRole(role);
        if (avatar != null) {
            accountGetDetailsVO.setAvatar(avatar);
        }
        if (telephone != null) {
            accountGetDetailsVO.setTelephone(telephone);
        }
        if (email != null) {
            accountGetDetailsVO.setEmail(email);
        }
        if (location != null) {
            accountGetDetailsVO.setLocation(location);
        }
        return accountGetDetailsVO;
    }
}
