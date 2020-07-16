package com.ppdai.auth.controller;

import com.ppdai.auth.common.constant.GrantType;
import com.ppdai.auth.common.identity.OAuth2Identity;
import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.utils.JwtUtil;
import com.ppdai.auth.exception.UnAuthorizeException;
import com.ppdai.auth.service.ApprovedSiteService;
import com.ppdai.auth.service.OAuth2Service;
import com.ppdai.auth.common.utils.BasicAuthUtil;
import com.ppdai.auth.utils.RequestContextUtil;
import com.ppdai.auth.vo.AuthCodeVO;
import com.ppdai.auth.vo.ClientVO;
import com.ppdai.auth.vo.ValidityVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;


/**
 * OAuth2授权框架(OAuth2 Authorization Framework)的后台接口
 *
 */
@RestController
@RequestMapping("/oauth2")
public class OAuth2Endpoint {

    @Autowired
    private OAuth2Service oAuth2Service;
    @Autowired
    private ApprovedSiteService approvedSiteService;

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public ResponseEntity<AuthCodeVO> oAuth2Authorize(@RequestParam(name="client_id") String clientId,
                                                   @RequestParam(name = "response_type") String responseType,
                                                   @RequestParam(name = "redirect_uri") String redirectUri,
                                                   @RequestParam(name = "state") String state) {
        String userName = RequestContextUtil.getCurrentUserName();

        ClientVO clientVO = new ClientVO();
        clientVO.setClientId(clientId);
        clientVO.setRespType(responseType);
        clientVO.setRedirectUrl(redirectUri);
        Set<String> scopes = new HashSet<>();
        scopes.add("user_role");
        clientVO.setScopes(scopes);

        AuthCodeVO code = oAuth2Service.authorize(userName, clientVO);

        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @ApiOperation(value = "OAuth2授权点", notes = "授权点颁发authorization code")
    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public ResponseEntity<AuthCodeVO> authorize(@RequestBody ClientVO clientVO) {
        String userName = RequestContextUtil.getCurrentUserName();
        AuthCodeVO code = oAuth2Service.authorize(userName, clientVO);

        // record the user's approval choice to the client
        String rememberChoice = clientVO.getRememberChoice();
        if (rememberChoice != null && rememberChoice.toLowerCase().equals("yes")) {
            approvedSiteService.record(userName, clientVO.getClientId(), clientVO.getScopes());
        }

        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @ApiOperation(value = "OAuth2令牌颁发点", notes = "颁发令牌access/refresh token")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public ResponseEntity<OAuth2AccessToken> issueToken(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                        @RequestParam(value = "grant_type", required = true) String grantType,
                                                        @RequestParam(value = "code", required = false) String authCode,
                                                        @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                                                        @RequestParam(value = "refresh_token", required = false) String refreshToken,
                                                        @RequestParam(name = "username", required = false) String userName,
                                                        @RequestParam(name = "password", required = false) String userPwd) {
        OAuth2AccessToken accessToken = null;

        String[] clientInfo = BasicAuthUtil.decode(authorization);
        if (clientInfo != null && clientInfo.length == 2) {
            String clientId = clientInfo[0];
            String clientSecret = clientInfo[1];

            GrantType gType = GrantType.valueOf(grantType.toUpperCase());
            if (gType == GrantType.AUTHORIZATION_CODE) {
                // 使用授权码来获取access token
                accessToken = oAuth2Service.issueToken(authCode, clientId, clientSecret);
            } else if (gType == GrantType.REFRESH_TOKEN) {
                // 使用refresh token来获取新的access token
                accessToken = oAuth2Service.refreshToken(refreshToken, clientId, clientSecret);
            } else if (gType == GrantType.CLIENT_CREDENTIALS) {
                accessToken = oAuth2Service.issueToken(clientId, clientSecret);
            } else if (gType == GrantType.PASSWORD) {
                // 实现使用client id/secret + 用户名和密码来获取token
                accessToken = oAuth2Service.issueToken(userName, userPwd, clientId, clientSecret);
            }
        }

        if (accessToken == null) {
            throw new UnAuthorizeException(MessageType.ERROR, "无法颁发令牌，请检查用户信息和授权码。");
        }

        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @ApiOperation(value = "OAuth2令牌检查点", notes = "检查令牌")
    @RequestMapping(value = "/introspect", method = RequestMethod.POST)
    public ResponseEntity<ValidityVO> introspectToken(@RequestParam(value = "token", required = true) String token) {
        ValidityVO tokenValidity = oAuth2Service.introspectToken(token);
        return new ResponseEntity<>(tokenValidity, HttpStatus.OK);
    }

    @ApiOperation(value = "OAuth2令牌吊销点", notes = "吊销令牌")
    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    public ResponseEntity<Boolean> revokeToken(@RequestHeader(value = "Authorization", required = false) String authorization,
                                               @RequestParam(value = "token", required = true) String token) {
        Boolean bSuccess = oAuth2Service.revokeToken(token);
        return new ResponseEntity<>(bSuccess, HttpStatus.OK);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<OAuth2Identity> fetchProfileByToken(@RequestHeader(value = "Authorization") String authorization) {
        OAuth2Identity identity = null;

        if (authorization != null) {
            String[] arr = authorization.split(" ");
            if (arr.length == 2) {
                String token = arr[1];
                identity = JwtUtil.oAuth2decode(token);
            }
        }

        return new ResponseEntity<>(identity, HttpStatus.OK);
    }

}
