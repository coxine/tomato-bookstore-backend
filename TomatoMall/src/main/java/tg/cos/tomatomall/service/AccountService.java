package tg.cos.tomatomall.service;

import tg.cos.tomatomall.dto.AccountDTO;
import tg.cos.tomatomall.vo.AccountGetDetailsVO;

public interface AccountService {
    AccountGetDetailsVO getUserDetails(String username);
    String createUser(AccountDTO accountDTO);
    String login(String username, String password);
    String updateUser(AccountDTO accountDTO);
    String changePassword(AccountDTO accountDTO);
}
