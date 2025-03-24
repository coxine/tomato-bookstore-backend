package tg.cos.tomatomall.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tg.cos.tomatomall.po.Account;

@Component
public class SecurityUtil {

    @Autowired
    HttpServletRequest httpServletRequest;

    public Account getCurrentUser(){
        System.out.println("approach securityUtil");
        System.out.println((Account) httpServletRequest.getSession().getAttribute("currentUser"));
        return (Account) httpServletRequest.getSession().getAttribute("currentUser");
    }
}
