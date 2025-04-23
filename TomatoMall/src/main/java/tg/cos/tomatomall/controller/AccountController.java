package tg.cos.tomatomall.controller;

import org.springframework.web.bind.annotation.*;

import tg.cos.tomatomall.service.AccountService;
import tg.cos.tomatomall.dto.AccountDTO;
import tg.cos.tomatomall.vo.AccountGetDetailsVO;
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

        AccountGetDetailsVO accountGetDetailsVO = accountService.getUserDetails(username);
        if (accountGetDetailsVO != null) {
            return Response.buildSuccess(accountGetDetailsVO);
        }
        return Response.buildFailure("用户不存在","400");
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response<?> createUser(@RequestBody AccountDTO accountDTO) {
        String result = accountService.createUser(accountDTO);
        if ("注册成功".equals(result)) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result,"400");
    }

    /**
     * 更新用户信息
     */
    @PutMapping()
    public Response<?> updateUser(@RequestBody AccountDTO accountDTO) {

        String result = accountService.updateUser(accountDTO);
        if ("用户信息更新成功".equals(result)) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result,"400");

    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response<?> login(@RequestBody AccountDTO accountDTO) {
        String result = accountService.login(accountDTO.getUsername(), accountDTO.getPassword());

        if (!"用户不存在/用户密码错误".equals(result)) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result,"400");
    }


    @PutMapping("/password")
    public Response<?> changePassword(@RequestBody AccountDTO accountDTO) {
        String result = accountService.changePassword(accountDTO);
        if ("密码修改成功".equals(result)) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result, "400");
    }
}
