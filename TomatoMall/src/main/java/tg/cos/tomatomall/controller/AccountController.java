package tg.cos.tomatomall.controller;

import org.springframework.web.bind.annotation.*;

import tg.cos.tomatomall.service.AccountService;
import tg.cos.tomatomall.vo.Response;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;

    /**
     * 获取用户详情
     */
    @GetMapping()
    public Response getUser() {
        return null;
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response createUser() {
        return null;
    }

    /**
     * 更新用户信息
     */
    @PutMapping()
    public Response updateUser() {
        return null;
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response login() {
        return null;
    }
}
