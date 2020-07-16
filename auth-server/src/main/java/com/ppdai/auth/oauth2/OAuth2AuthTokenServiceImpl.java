package com.ppdai.auth.oauth2;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.utils.JwtUtil;
import com.ppdai.auth.dao.*;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.exception.UnAuthorizeException;
import com.ppdai.auth.idp.AuthenticationProvider;
import com.ppdai.auth.common.identity.Identity;
import com.ppdai.auth.idp.local.LocalAuthenticationProvider;
import com.ppdai.auth.po.*;
import com.ppdai.auth.scheduler.OAuth2TokenExpireUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * A implementation of authorization server token service
 *
 */
@Service
@Slf4j
public class OAuth2AuthTokenServiceImpl implements AuthorizationServerTokenServices {

    @Autowired
    @Qualifier(value = "localIdpProvider")
    private AuthenticationProvider localIdpProvider;
    @Autowired
    @Qualifier(value = "atlasLdpProvider")
    private AuthenticationProvider atlasLdpProvider;
    @Autowired
    private AccessTokenRepository accessTokenRepo;
    @Autowired
    private RefreshTokenRepository refreshTokenRepo;
    @Autowired
    private AuthenticationHolderRepository authHolderRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private OAuth2TokenExpireUtil tokenExpireUtil;

    public OAuth2AuthTokenServiceImpl() {
    }

    @Transactional(rollbackFor = BaseException.class)
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication o2Authentication) throws AuthenticationException {
        String clientId = o2Authentication.getOAuth2Request().getClientId();

        //String identityName = null;
        Identity identity = null;
        if (o2Authentication.isClientOnly()) {
            identity = loadIdentity(clientId, Boolean.TRUE);
        } else {
            // other grant types
            String userName = o2Authentication.getPrincipal().toString();
            identity = loadIdentity(userName, Boolean.FALSE);
        }

        // throw an exception when the identity doesn't exist
        if (identity == null) {
            throw UnAuthorizeException.newException(MessageType.ERROR, "该用户不存在。");
        }
        identity.fillAdditionalInfomation("client_id", clientId);

        // 从extensions中获取token的有效期
        Timestamp accTsExpire = tokenExpireUtil.getAccessTokenExpireTime();
        Timestamp refTsExpire = tokenExpireUtil.getRefreshTokenExpireTime();
        String accToken = generateJwtToken(identity, accTsExpire);
        String refToken = generateJwtToken(identity, refTsExpire);

        // store the following entities into repository
        // 1 - authHolder entity
        // 2 - refresh token entity
        // 3 - access token entity
        String userName = o2Authentication.getPrincipal().toString();
        UserEntity user = userRepo.findOneByName(userName);
        Long userId = user != null ? user.getId() : null;
        AccessTokenEntity o2AccessToken = new AccessTokenEntity();
        AuthenticationHolderEntity authHolder = new AuthenticationHolderEntity();
        authHolder.setAuthentication(o2Authentication);
        authHolder.setUserId(userId);
        authHolderRepo.save(authHolder);

        // when in client credential grant mode, there is no need to release a refresh token
        // create the refresh token when it's not client credential grant type, otherwise skip
        if (!o2Authentication.isClientOnly()) {
            RefreshTokenEntity refreshToken = new RefreshTokenEntity();
            refreshToken.setTokenValue(refToken);
            refreshToken.setClientId(clientId);
            refreshToken.setExpiration(refTsExpire);
            refreshToken.setUserId(userId);
            refreshTokenRepo.save(refreshToken);
            refreshToken.setAuthHolderId(authHolder.getId());
            o2AccessToken.setRefreshToken(refreshToken);
        }

        o2AccessToken.setAuthHolderId(authHolder.getId());
        o2AccessToken.setTokenValue(accToken);
        o2AccessToken.setExpiration(accTsExpire);
        o2AccessToken.setUserId(userId);
        o2AccessToken.setClientId(clientId);
        o2AccessToken = accessTokenRepo.save(o2AccessToken);

        return o2AccessToken;
    }

    @Transactional(rollbackFor = BaseException.class)
    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
        // remove any expired refresh token
        tokenExpireUtil.removeRefreshToken();

        // check the refresh token
        RefreshTokenEntity refreshToken = refreshTokenRepo.findByTokenEx(refreshTokenValue);
        if (refreshToken == null) {
            throw UnAuthorizeException.newException(MessageType.ERROR, "refresh token无效，请重新登录。");
        }

        // check if the client own the refresh token
        String cid = refreshToken.getClientId();
        String clientId = tokenRequest.getClientId();
        if (!clientId.equals(cid)) {
            throw new UnAuthorizeException(MessageType.ERROR, "Client does not own the presented refresh token");
        }

        // check if the refresh token is expired
//        if (refreshToken.getExpiration().before(new Date())) {
//            throw new BaseException(MessageType.ERROR, "Expired refresh token:" + refreshTokenValue);
//        }

        // 生成新token前吊销旧token
        Iterable<AccessTokenEntity> accessTokens = accessTokenRepo.findByRefreshToken(refreshToken);
        for (AccessTokenEntity accessToken : accessTokens) {
            accessTokenRepo.removeById(accessToken.getId());
        }

        // read the identity name
        Long authHolderId = refreshToken.getAuthHolderId();
        AuthenticationHolderEntity authHolder = authHolderRepo.findOne(authHolderId);
        Long userId = authHolder.getUserId();

        Identity identity = null;
        if (userId != null) {
            UserEntity user = userRepo.findOne(userId);
            identity = loadIdentity(user.getName(), Boolean.FALSE);
        } else {
            identity = loadIdentity(clientId, Boolean.TRUE);
        }

        // throw an exception when the identity doesn't exist
        if (identity == null) {
            throw UnAuthorizeException.newException(MessageType.ERROR, "failed to read identity info");
        }
        identity.fillAdditionalInfomation("clientId", clientId);

        // generate the token by identity
        Timestamp accTsExpire = tokenExpireUtil.getAccessTokenExpireTime();
        String accToken = generateJwtToken(identity, accTsExpire);

        // issue access token
        AccessTokenEntity o2AccessToken = new AccessTokenEntity();
        o2AccessToken.setAuthHolderId(authHolder.getId());
        o2AccessToken.setTokenValue(accToken);
        o2AccessToken.setExpiration(accTsExpire);
        o2AccessToken.setUserId(userId);
        o2AccessToken.setClientId(clientId);
        o2AccessToken.setRefreshToken(refreshToken);
        o2AccessToken = accessTokenRepo.save(o2AccessToken);

        return o2AccessToken;
    }

    private Identity loadIdentity(String identityName, Boolean isClient) {
        Identity identity = null;

        // 先从远程获取获取用户信息，若没有找到，则尝试从本地数据库获取
        identity = atlasLdpProvider.load(identityName);
        if (identity == null) {
            if (isClient && localIdpProvider.getClass() == LocalAuthenticationProvider.class) {
                // client credential grant type
                String clientId = identityName;
                identity = ((LocalAuthenticationProvider) localIdpProvider).loadClientIdentity(clientId);
            } else {
                // other grant types
                identity = localIdpProvider.load(identityName);
            }
        }

        return identity;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication oAuth2Authentication) {
        return null;
    }

    public OAuth2AccessToken getAccessToken(String token) {
        // remove any expired access token
        tokenExpireUtil.removeAccessToken();
        return accessTokenRepo.findByTokenEx(token);
    }

    public OAuth2RefreshToken getRefreshToken(String token) {
        // remove any expired refresh token
        tokenExpireUtil.removeRefreshToken();
        return refreshTokenRepo.findByTokenEx(token);
    }

    @Transactional(rollbackFor = BaseException.class)
    public Boolean revokeAccessToken(String token) {
        Boolean bSuccess = Boolean.FALSE;

        AccessTokenEntity accessToken = accessTokenRepo.findByToken(token);
        if (accessToken != null) {
            if (accessToken.getIsActive() == true) {
                OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
                if (refreshToken != null) {
                    revokeRefreshToken(refreshToken.getValue());
                }
                accessTokenRepo.removeById(accessToken.getId());
            }
            bSuccess = Boolean.TRUE;
        }

        return bSuccess;
    }

    @Transactional(rollbackFor = BaseException.class)
    public Boolean revokeRefreshToken(String token) {
        Boolean bSuccess = Boolean.FALSE;

        RefreshTokenEntity refreshToken = refreshTokenRepo.findByToken(token);
        if (refreshToken != null) {
            if (refreshToken.getIsActive() == true) {
                refreshTokenRepo.removeById(refreshToken.getId());
            }
            bSuccess = Boolean.TRUE;
        }

        return bSuccess;
    }

    /**
     * 生成Json Web Token格式的令牌
     *
     * @param identity 用户信息
     * @return 令牌
     */
    private String generateJwtToken(Identity identity, Timestamp expireTime) {
        return JwtUtil.encode(identity, expireTime, "secret", "pauth");
    }

}
