package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.dto.AccountDTO;

@Getter
@Setter
public class AccountGetDetailsVO {
    private String username;
    private String name;
    private String role;
    private String avatar;
    private String telephone;
    private String email;
    private String location;
    /**
     * 将VO对象转换为DTO对象
     * @return AccountDTO对象
     */
    public AccountDTO toDTO() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUsername(this.username);
        accountDTO.setName(this.name);
        accountDTO.setRole(this.role);
        if (this.avatar != null) {
            accountDTO.setAvatar(this.avatar);
        }
        if (this.telephone != null) {
            accountDTO.setTelephone(this.telephone);
        }
        if (this.email != null) {
            accountDTO.setEmail(this.email);
        }
        if (this.location != null) {
            accountDTO.setLocation(this.location);
        }
        return accountDTO;
    }

}
