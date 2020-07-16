package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2 Scope的后台管理接口
 *
 */
@RestController
@RequestMapping("/api/scopes")
public class ScopeMgtController {

    @ApiOperation(value = "获取OAuth2 Scope列表")
    @RequestMapping(method = RequestMethod.GET)
    public Response<String> getScopeList() {
        return Response.mark(MessageType.SUCCESS, "fetch scope list");
    }

}
