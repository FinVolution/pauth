package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.auth.po.ApprovedSiteEntity;
import com.ppdai.auth.service.ApprovedSiteService;
import com.ppdai.auth.vo.PageVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth2登录session的后台管理接口
 *
 */
@RestController
@RequestMapping("/api/sessions")
@Slf4j
public class SessionMgtController {

    @Autowired
    private ApprovedSiteService approvedSiteService;

    @ApiOperation(value = "获取用户登录session列表")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Response<Iterable<ApprovedSiteEntity>> getSessionList(@RequestParam(value = "username") String username) {
        Iterable<ApprovedSiteEntity> sites = approvedSiteService.getSitesByUserName(username);
        return Response.mark(MessageType.SUCCESS, sites);
    }

    @ApiOperation(value = "获取登录session分页列表")
    @RequestMapping(method = RequestMethod.GET)
    public Response<PageVO<ApprovedSiteEntity>> getSessionsByPage(@RequestParam String username,
                                                                  @RequestParam String clientId,
                                                                  @RequestParam Integer page,
                                                                  @RequestParam Integer size) {
        PageVO<ApprovedSiteEntity> sitePageVO = approvedSiteService.getSitesByPage(username, clientId, page, size);
        return Response.mark(MessageType.SUCCESS, sitePageVO);
    }

    @ApiOperation(value = "注销用户登录session", notes = "注销用户登录session，让用户申请token时重新验证通过。")
    @RequestMapping(value = "/{sessionId}", method = RequestMethod.DELETE)
    public Response<String> revokeSessionById(@PathVariable Long sessionId) {
        approvedSiteService.removeSiteById(sessionId);
        return Response.mark(MessageType.SUCCESS, "用户登录session[id=%s]已被注销。", sessionId);
    }

}
