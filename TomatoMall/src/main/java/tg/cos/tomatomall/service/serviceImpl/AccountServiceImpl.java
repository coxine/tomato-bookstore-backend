package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.stereotype.Service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.repository.AccountRepository;
import tg.cos.tomatomall.service.AccountService;
import tg.cos.tomatomall.vo.AccountVO;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AccountVO getUserDetails(String username) {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isPresent()) {
            return accountOptional.get().toVO();
        }
        return null;
    }

    @Override
    public String createUser(AccountVO accountVO) {
        if (accountRepository.existsByUsername(accountVO.getUsername())) {
            return "用户名已存在";
        }
        Account account = accountVO.toAccount();
        accountRepository.save(account);
        return "创建用户成功";
    }

    @Override
    public String login(String username, String password) {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isPresent() && accountOptional.get().getPassword().equals(password)) {
            return "登录成功";
        }
        return "用户不存在/用户密码错误";
    }

    @Override
    public String updateUser(AccountVO accountVO) {
        Optional<Account> accountOptional = accountRepository.findByUsername(accountVO.getUsername());
        if (accountOptional.isPresent()) {
            Account account = accountVO.toAccount();
            accountRepository.save(account);
            return "用户信息更新成功";
        }
        return "用户不存在";
    }
}
