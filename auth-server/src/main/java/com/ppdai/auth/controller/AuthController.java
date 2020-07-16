package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.auth.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户的注册和登录接口
 *
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 根据Token信息设置密码
     *
     * @param token 密码设置信息
     * @return
     */
    @ApiOperation(value = "设置用户密码", notes = "根据Token信息设置密码")
    @RequestMapping(value = "/setPassword", method = RequestMethod.POST)
    public Response<String> setUserPasswordByToken(@RequestBody String token) {
        Boolean bSuccess = userService.setPasswordByToken(token);
        return bSuccess ? Response.mark(MessageType.SUCCESS, "用户密码设置成功。") : Response.mark(MessageType.ERROR, "用户密码设置失败。");
    }

    /**
     * 使用用户邮箱设置用户名
     *
     * @param email    用户邮箱地址
     * @param username 用户名
     * @return
     */
    @ApiOperation(value = "设置用户信息", notes = "根据当前登录用户设置用户信息")
    @RequestMapping(value = "/setUserInfo", method = RequestMethod.POST)
    public Response<String> setUserInfo(@RequestParam(value = "email") String email, @RequestParam(value = "username") String username) {
        String jwtToken = userService.setUserInfo(email, username);
        return jwtToken != null ? Response.mark(MessageType.SUCCESS, jwtToken) : Response.mark(MessageType.ERROR, "用户信息设置失败。");
    }

    /**
     * 根据用户登录信息Token进行登录验证，如果登录成功，则颁发JwtToken
     *
     * @param loginToken 用户登录信息
     * @return 如果登录成功，返回JwtToken
     */
    @ApiOperation(value = "用户登录接口", notes = "根据用户登录信息Token进行登录验证，如果登录成功，则颁发JwtToken")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Response<String> loginByToken(@RequestBody String loginToken) {
        String jwtToken = userService.loginUserByToken(loginToken);
        return jwtToken != null ? Response.mark(MessageType.SUCCESS, jwtToken) : Response.mark(MessageType.ERROR, "对不起，用户登录失败。");
    }
}
