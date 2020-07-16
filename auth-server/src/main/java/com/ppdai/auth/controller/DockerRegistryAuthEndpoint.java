package com.ppdai.auth.controller;

import com.ppdai.auth.common.constant.GrantType;
import com.ppdai.auth.po.AccessTokenEntity;
import com.ppdai.auth.service.OAuth2Service;
import com.ppdai.auth.common.utils.BasicAuthUtil;
import com.ppdai.auth.utils.EnvProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 该controller提供专门为docker registry的oauth2认证服务。
 * <p>
 * 详细流程见：
 * https://docs.docker.com/registry/spec/auth/oauth/
 * 注：
 * 1. docker registry通过password获取token的方法和标准OAuth2流程不一致，当前controller专门为这个流程做了相应的调整。
 * 主要区别为：
 * 1) grant_type没有明确指定为password，而且该值不一定会被指定值
 * 2) 用户名和密码没有在request parameter中指定
 * 3）没有client Id和credential
 * 4）Authorization Header被用于传用户名和密码，在OAuth2标准中应该传入client Id和credential
 * <p>
 * 2. docker registry通过refresh_token获取token的方法和标准OAuth2流程一致
 *
 */
@RestController
@RequestMapping("/oauth2/docker")
@Slf4j
public class DockerRegistryAuthEndpoint {

    @Autowired
    private EnvProperty envProperty;
    @Autowired
    private OAuth2Service oAuth2Service;

    @ApiOperation(value = "Docker Registry的OAuth2令牌颁发点", notes = "颁发令牌access/refresh token")
    @RequestMapping(value = "/token", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<OAuth2AccessToken> issueToken(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                        @RequestParam(name = "offline_token", required = false) String offlineToken,
                                                        @RequestParam(name = "grant_type", required = false) String grantType,
                                                        @RequestParam(name = "client_id", required = false) String clientId,
                                                        @RequestParam(name = "access_type", required = false) String accessType,
                                                        @RequestParam(name = "refresh_token", required = false) String refreshToken,
                                                        @RequestParam(name = "username", required = false) String username,
                                                        @RequestParam(name = "password", required = false) String password,
                                                        @RequestParam("service") String service,
                                                        @RequestParam(name = "scope", required = false) String scope) {
        log.info("Authorization:{}, offline_token:{},grant_type:{},client_id:{},access_type:{},refresh_token:{},username:{},password:{},service:{},scope:{}",
                authorization, offlineToken, grantType, clientId, accessType, refreshToken, username, password, service, scope);

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String value = httpServletRequest.getHeader("Authorization");
        System.out.println(" header [ Authorization ---> " + value + " ]");

        OAuth2AccessToken accessToken = null;
        String dockerClientId = envProperty.getProperty("app.docker.registry.clientId");
        String dockerClientSecret = envProperty.getProperty("app.docker.registry.clientSecret");

        if (grantType != null && GrantType.valueOf(grantType.toUpperCase()) == GrantType.REFRESH_TOKEN) {
            // docker-deamon如果发来的是REFRESH_TOKEN类型，直接返回原来的token
            AccessTokenEntity o2AccessToken = new AccessTokenEntity();
            o2AccessToken.setTokenValue(refreshToken);
            accessToken = o2AccessToken;
        } else {
            // 实现使用client id/secret + 用户名和密码来获取token
            // 注：docker registry认证方法和oAuth2标准不一致，用户名和密码来自basic auth，而不是来自query parameter，下面有作相应的调整
            String[] clientInfo = BasicAuthUtil.decode(authorization);
            if (clientInfo != null && clientInfo.length == 2) {
                // try with password grant type
                // workaround: use fixed client id/secret for docker oauth2 authentication
                String user_name = clientInfo[0];
                String user_pwd = clientInfo[1];
                accessToken = oAuth2Service.issueToken(user_name, user_pwd, dockerClientId, dockerClientSecret);
            }
        }

        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

}
