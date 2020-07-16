package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.auth.service.UserService;
import com.ppdai.auth.vo.PageVO;
import com.ppdai.auth.vo.UserVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户的后台管理接口
 *
 */
@RestController
@RequestMapping("/api/users")
public class UserMgtController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取用户列表")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Response<List<UserVO>> getAllUsers() {
        List<UserVO> users = userService.getAllUsers();
        return Response.mark(MessageType.SUCCESS, users);
    }

    @ApiOperation(value = "获取分页用户列表")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Response<PageVO<UserVO>> getUsersByPage(@RequestParam String name,
                                                   @RequestParam Integer page,
                                                   @RequestParam Integer size) {
        PageVO<UserVO> userPageVO = userService.getUsersByPage(name, page, size);
        return Response.mark(MessageType.SUCCESS, userPageVO);
    }
}
