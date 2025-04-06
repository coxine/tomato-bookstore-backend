package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.repository.AccountRepository;
import tg.cos.tomatomall.service.AccountService;
import tg.cos.tomatomall.dto.AccountDTO;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.util.TokenUtil;

import java.util.Date;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tg.cos.tomatomall.vo.AccountGetDetailsVO;
import java.util.regex.Pattern;



@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();  // 创建加密器

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    public boolean containsInvalidCharacters(String input) {
        // 定义允许的字符集正则表达式
        String allowedPattern = "^[a-zA-Z0-9!@#$%^&*()\\-_+=]*$";

        // 如果字符串不匹配允许的模式，则包含非法字符
        return !Pattern.matches(allowedPattern, input);
    }

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
        if (containsInvalidCharacters(accountDTO.getUsername()) || containsInvalidCharacters(accountDTO.getPassword())) {
            return "输入不合法";
        }
        if (accountRepository.existsByUsername(accountDTO.getUsername())) {
            return "用户名已存在";
        }
        accountDTO.setRole(accountDTO.getRole().toUpperCase());
        if (!accountDTO.getRole().equals("ADMIN") && !accountDTO.getRole().equals("USER")) {
            return "输入不合法";
        }
        Account account = accountDTO.toPO();
        String encodedPassword = passwordEncoder.encode(accountDTO.getPassword());
        account.setPassword(encodedPassword);
        account.setLatestAvatarChangeTime(new Date());
        accountRepository.save(account);
        return "注册成功";
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
        Account accountFromToken=securityUtil.getCurrentUser();
        if (containsInvalidCharacters(accountDTO.getUsername())) {
            return "输入不合法";
        }
        Account account;
        Optional<Account> accountOptional = accountRepository.findByUsername(accountDTO.getUsername());
        if (accountOptional.isPresent()) {
            account = accountOptional.get();
        }else {
            return "用户不存在";
        }
        if (!accountDTO.getUsername().equals(accountFromToken.getUsername())) {
            return "token与输入的用户名不匹配";
        }
        // 只更新非空字段
//            if (accountDTO.getUsername() != null) account.setUsername(accountDTO.getUsername());
        if (accountDTO.getPassword() != null)
        {
            if (containsInvalidCharacters(accountDTO.getPassword())) {
                return "输入不合法";
            }
            String encodedPassword = passwordEncoder.encode(accountDTO.getPassword());
            account.setPassword(encodedPassword);
        }
        if (accountDTO.getName() != null) account.setName(accountDTO.getName());
        if (accountDTO.getAvatar() != null) {
            account.setAvatar(accountDTO.getAvatar());
            account.setLatestAvatarChangeTime(new Date());
        }
        if (accountDTO.getTelephone() != null) account.setTelephone(accountDTO.getTelephone());
        if (accountDTO.getEmail() != null) account.setEmail(accountDTO.getEmail());
        if (accountDTO.getLocation() != null) account.setLocation(accountDTO.getLocation());
        accountRepository.save(account); // 更新已有用户
        return "用户信息更新成功";
    }

}
