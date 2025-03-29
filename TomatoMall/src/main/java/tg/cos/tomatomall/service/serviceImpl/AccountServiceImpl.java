package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.repository.AccountRepository;
import tg.cos.tomatomall.service.AccountService;
import tg.cos.tomatomall.dto.AccountDTO;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.util.TokenUtil;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tg.cos.tomatomall.vo.AccountGetDetailsVO;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  // 创建加密器

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;
    @Override

    public AccountGetDetailsVO getUserDetails(String username) {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isPresent()) {
            AccountDTO accountDTO = accountOptional.get().toDTO();
            AccountGetDetailsVO accountGetDetailsVO = accountDTO.toGetDetailsVO();
            return accountGetDetailsVO;
        }
        return null;
    }

    @Override
    public String createUser(AccountDTO accountDTO) {
        if (accountRepository.existsByUsername(accountDTO.getUsername())) {
            return "用户名已存在";
        }
        Account account = accountDTO.toPO();
        String encodedPassword = passwordEncoder.encode(accountDTO.getPassword());
        account.setPassword(encodedPassword);
        accountRepository.save(account);
        return "创建用户成功";
    }

    @Override
    public String login(String username, String password) {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isPresent() && passwordEncoder.matches(password, accountOptional.get().getPassword())) {
            return tokenUtil.getToken(accountOptional.get());
        }
        return "用户不存在/用户密码错误";
    }

    @Override
    public String updateUser(AccountDTO accountDTO) {
//        System.out.println("approach impl");
        Account account=securityUtil.getCurrentUser();
        if(account!=null) {    // 只更新非空字段
            if (accountDTO.getUsername() != null) account.setUsername(accountDTO.getUsername());
            if (accountDTO.getPassword() != null)
            {
                String encodedPassword = passwordEncoder.encode(accountDTO.getPassword());
                account.setPassword(encodedPassword);
            }
            if (accountDTO.getName() != null) account.setName(accountDTO.getName());
            if (accountDTO.getAvatar() != null) account.setAvatar(accountDTO.getAvatar());
            if (accountDTO.getTelephone() != null) account.setTelephone(accountDTO.getTelephone());
            if (accountDTO.getEmail() != null) account.setEmail(accountDTO.getEmail());
            if (accountDTO.getLocation() != null) account.setLocation(accountDTO.getLocation());
            accountRepository.save(account); // 更新已有用户
            return "用户信息更新成功";
        }
        return "用户不存在";
    }

}
