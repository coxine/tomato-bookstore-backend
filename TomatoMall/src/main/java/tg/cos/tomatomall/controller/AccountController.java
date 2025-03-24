package tg.cos.tomatomall.controller;

import org.springframework.web.bind.annotation.*;

import tg.cos.tomatomall.service.AccountService;
import tg.cos.tomatomall.vo.AccountVO;
import tg.cos.tomatomall.vo.Response;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response<?> getUser(@PathVariable("username") String username) {
        AccountVO accountVO = accountService.getUserDetails(username);
        if (accountVO != null) {
            return Response.buildSuccess(accountVO);
        }
        return Response.buildFailure("用户不存在","1000");
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response<?> createUser(@RequestBody AccountVO accountVO) {
        String result = accountService.createUser(accountVO);
        if ("创建用户成功".equals(result)) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result,"1001");
    }

    /**
     * 更新用户信息
     */
    @PutMapping()
    public Response<?> updateUser(@RequestBody AccountVO accountVO) {

        String result = accountService.updateUser(accountVO);
        if ("用户信息更新成功".equals(result)) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result,"1002");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response<?> login(@RequestBody AccountVO accountVO) {
        String result = accountService.login(accountVO.getUsername(), accountVO.getPassword());
        if (!"用户不存在/用户密码错误".equals(result)) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result,"1003");
    }
}
