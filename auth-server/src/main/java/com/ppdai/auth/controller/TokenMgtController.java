package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.auth.manager.TokenManager;
import com.ppdai.auth.manager.UserOwnedRrcManager;
import com.ppdai.auth.vo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * OAuth2 access token/refresh token的后台管理接口
 *
 */
@RestController
@RequestMapping("/api")
public class TokenMgtController {

    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private UserOwnedRrcManager userOwnedRrcManager;

    @ApiOperation(value = "获取access token列表")
    @RequestMapping(value = "/access_tokens/all", method = RequestMethod.GET)
    public Response<List<AuthTokenVO>> getAccessTokenList(@RequestParam(value = "username", required = false) String username) {
        List<AuthTokenVO> accessTokens = new ArrayList<>();
        if (username == null) {
            accessTokens = tokenManager.getAllAccessTokens();
        } else if (!username.equals("null")) {
            accessTokens = userOwnedRrcManager.getMyAccessTokens(username);
        }
        return Response.mark(MessageType.SUCCESS, accessTokens);
    }

    @ApiOperation(value = "获取access token分页列表")
    @RequestMapping(value = "/access_tokens", method = RequestMethod.GET)
    public Response<PageVO<AuthTokenVO>> getAccessTokensByPage(@RequestParam Long userId,
                                                               @RequestParam String clientId,
                                                               @RequestParam Integer page,
                                                               @RequestParam Integer size) {
        PageVO<AuthTokenVO> accessTokenPageVO = tokenManager.getAccessTokensByPage(userId, clientId, page, size);
        return Response.mark(MessageType.SUCCESS, accessTokenPageVO);
    }

    @ApiOperation(value = "吊销access token")
    @RequestMapping(value = "/access_tokens/{tokenId}", method = RequestMethod.DELETE)
    public Response<String> revokeAccessTokenById(@PathVariable Long tokenId) {
        tokenManager.revokeAccessToken(tokenId);
        return Response.mark(MessageType.SUCCESS, "访问令牌id=%s已经被注销", tokenId);
    }

    @ApiOperation(value = "获取refresh token列表")
    @RequestMapping(value = "/refresh_tokens/all", method = RequestMethod.GET)
    public Response<List<AuthTokenVO>> getRefreshTokenList(
            @RequestParam(value = "username", required = false) String username) {
        List<AuthTokenVO> refreshTokens = new ArrayList<>();
        if (username == null) {
            refreshTokens = tokenManager.getAllRefreshTokens();
        } else if (!username.equals("null")) {
            refreshTokens = userOwnedRrcManager.getMyRefreshTokens(username);
        }
        return Response.mark(MessageType.SUCCESS, refreshTokens);
    }

    @ApiOperation(value = "获取refresh token分页列表")
    @RequestMapping(value = "/refresh_tokens", method = RequestMethod.GET)
    public Response<PageVO<AuthTokenVO>> getRefreshTokensByPage(@RequestParam Long userId,
                                                                @RequestParam String clientId,
                                                                @RequestParam Integer page,
                                                                @RequestParam Integer size) {
        PageVO<AuthTokenVO> refreshTokenPageVO = tokenManager.getRefreshTokensByPage(userId, clientId, page, size);
        return Response.mark(MessageType.SUCCESS, refreshTokenPageVO);
    }

    @ApiOperation(value = "吊销refresh token")
    @RequestMapping(value = "/refresh_tokens/{tokenId}", method = RequestMethod.DELETE)
    public Response<String> revokeRefreshTokenById(@PathVariable Long tokenId) {
        tokenManager.revokeRefreshToken(tokenId);
        return Response.mark(MessageType.SUCCESS, "刷新令牌id=%s已经被注销", tokenId);
    }

    @ApiOperation(value = "获取auth codes列表")
    @RequestMapping(value = "/auth_codes/all", method = RequestMethod.GET)
    public Response<List<AuthCodeVO>> getAuthCodeList(
            @RequestParam(value = "username", required = false) String username) {
        List<AuthCodeVO> authCodes = new ArrayList<>();
        if (username == null) {
            authCodes = tokenManager.getAllAuthCodes();
        } else if (!username.equals("null")) {
            authCodes = userOwnedRrcManager.getMyAuthCodes(username);
        }
        return Response.mark(MessageType.SUCCESS, authCodes);
    }

    @ApiOperation(value = "获取auth code分页列表")
    @RequestMapping(value = "/auth_codes", method = RequestMethod.GET)
    public Response<PageVO<AuthCodeVO>> getAuthCodesByPage(@RequestParam Long userId,
                                                           @RequestParam String clientId,
                                                           @RequestParam Integer page,
                                                           @RequestParam Integer size) {
        PageVO<AuthCodeVO> authCodePageVO = tokenManager.getAuthCodesByPage(userId, clientId, page, size);
        return Response.mark(MessageType.SUCCESS, authCodePageVO);
    }

    @ApiOperation(value = "吊销auth code")
    @RequestMapping(value = "/auth_codes/{codeId}", method = RequestMethod.DELETE)
    public Response<String> revokeAuthCodeById(@PathVariable Long codeId) {
        tokenManager.revokeAuthCode(codeId);
        return Response.mark(MessageType.SUCCESS, "授权码id=%s已经被注销", codeId);
    }

    @ApiOperation(value = "生成长期有效的token")
    @RequestMapping(value = "/longtimeToken", method = {RequestMethod.POST})
    public Response<String> issueTokenWithLifeCycle(@RequestBody LongTimeTokenVO longTimeTokenVO){
        OAuth2AccessToken accessToken = tokenManager.createLongtimeToken(longTimeTokenVO.getUserName(), longTimeTokenVO.getClientId(), longTimeTokenVO.getLifeCycle());
        return Response.mark(MessageType.SUCCESS, "Token颁发成功。");
    }

    @ApiOperation(value = "延长token失效时间")
    @RequestMapping(value = "/access_tokens/extend", method = RequestMethod.POST)
    public Response<String> extendTokenExpiration(@RequestBody ExtendVO extendVO) {
        Long tokenId = extendVO.getTokenId();
        Long overtime = extendVO.getOvertime();
        tokenManager.extendTokenExpiration(tokenId, overtime);
        return Response.mark(MessageType.SUCCESS, "Token失效时间已延长。");
    }
}
