package tg.cos.tomatomall.service;

import tg.cos.tomatomall.vo.AccountVO;

public interface AccountService {
    AccountVO getUserDetails(String username);
    String createUser(AccountVO accountVO);
    String login(String username, String password);
    String updateUser(AccountVO accountVO);
}
