package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.repository.AccountRepository;
import tg.cos.tomatomall.service.AccountService;
import tg.cos.tomatomall.vo.AccountVO;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.util.TokenUtil;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
        String encodedPassword = passwordEncoder.encode(accountVO.getPassword());
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
    public String updateUser(AccountVO accountVO) {
//        System.out.println("approach impl");
        Account account=securityUtil.getCurrentUser();
        if(account!=null) {    // 只更新非空字段
            if (accountVO.getUsername() != null) account.setUsername(accountVO.getUsername());
            if (accountVO.getPassword() != null)
            {
                String encodedPassword = passwordEncoder.encode(accountVO.getPassword());
                account.setPassword(encodedPassword);
            }
            if (accountVO.getName() != null) account.setName(accountVO.getName());
            if (accountVO.getAvatar() != null) account.setAvatar(accountVO.getAvatar());
            if (accountVO.getTelephone() != null) account.setTelephone(accountVO.getTelephone());
            if (accountVO.getEmail() != null) account.setEmail(accountVO.getEmail());
            if (accountVO.getLocation() != null) account.setLocation(accountVO.getLocation());
            accountRepository.save(account); // 更新已有用户
            return "用户信息更新成功";
        }
        return "用户不存在";
    }

}
