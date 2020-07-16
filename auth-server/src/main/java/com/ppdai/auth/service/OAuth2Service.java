package com.ppdai.auth.service;

import com.ppdai.auth.vo.AuthCodeVO;
import com.ppdai.auth.vo.ClientVO;
import com.ppdai.auth.vo.ValidityVO;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * oauth2 service
 *
 */
public interface OAuth2Service {

    AuthCodeVO authorize(String userName, ClientVO clientVO);

    /**
     * 通过授权码获取access/refresh token
     *
     * @param code         授权码
     * @param clientId     申请token的client id
     * @param clientSecret 申请token的client secret
     * @return
     */
    OAuth2AccessToken issueToken(String code, String clientId, String clientSecret);

    /**
     * 通过用户名和密码获取access/refresh token
     *
     * @param username     用户名
     * @param password     用户密码
     * @param clientId     申请token的client id
     * @param clientSecret 申请token的client secret
     * @return
     */
    OAuth2AccessToken issueToken(String username, String password, String clientId, String clientSecret);


    /**
     * 通过client id/secret直接获取access/refresh token
     *
     * @param clientId     申请token的client id
     * @param clientSecret 申请token的client secret
     * @return
     */
    OAuth2AccessToken issueToken(String clientId, String clientSecret);

    OAuth2AccessToken refreshToken(String token, String clientId, String clientSecret);

    ValidityVO introspectToken(String token);

    Boolean revokeToken(String token);

}
