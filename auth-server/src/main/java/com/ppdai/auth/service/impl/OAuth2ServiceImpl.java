package com.ppdai.auth.service.impl;

import com.ppdai.auth.common.constant.AuthorizeResponseType;
import com.ppdai.auth.common.constant.GrantType;
import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.dao.AuthorizationCodeRepository;
import com.ppdai.auth.dao.ClientRepository;
import com.ppdai.auth.exception.UnAuthorizeException;
import com.ppdai.auth.oauth2.OAuth2AuthCodeServiceImpl;
import com.ppdai.auth.oauth2.OAuth2AuthTokenServiceImpl;
import com.ppdai.auth.po.AuthorizationCodeEntity;
import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.po.UserEntity;
import com.ppdai.auth.service.OAuth2Service;
import com.ppdai.auth.service.UserService;
import com.ppdai.auth.vo.AuthCodeVO;
import com.ppdai.auth.vo.ClientVO;
import com.ppdai.auth.vo.ValidityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Please add description here.
 *
 */
@Service
public class OAuth2ServiceImpl implements OAuth2Service {

    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private OAuth2AuthCodeServiceImpl oAuth2AuthCodeService;
    @Autowired
    private OAuth2AuthTokenServiceImpl oAuth2AuthTokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorizationCodeRepository authCodeRepo;

    @Override
    public AuthCodeVO authorize(String userName, ClientVO clientVO) {
        AuthCodeVO codeVO = null;

        // TODO: check user name and password with ldap, Note: currently this step is skipped as the user has been verified by filter
        // TODO: check if the user has the access to client

        // check client id
        String clientId = clientVO.getClientId();
        ClientEntity client = clientRepo.findByIdEx(clientId);
        if (client != null) {
            // TODO: check scopes, Note: currently there are fixed scopes by definition
            Set<String> scopes = clientVO.getScopes();

            // verify client redirect url
            // Note: skip the redirect url verification when in test environment
            String redirectUrl = clientVO.getRedirectUrl();

            // redirectUrl的正则表达式校验
            boolean isMatch = false;
            try {
                isMatch = Pattern.matches(client.getRedirectUrl(), redirectUrl);
            } catch (PatternSyntaxException e) {
                throw new UnAuthorizeException(MessageType.ERROR, "应用回调地址校验异常，请确保注册的地址格式正确。");
            }

            if (isMatch) {

                // start to issue an auth code
                OAuth2Request oAuth2Request = new OAuth2Request(null, clientId, null, true, scopes, null, null, null, null);
                String userPwd = null;
                UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken(userName, userPwd);
                OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, userAuthentication);

                codeVO = new AuthCodeVO();
                codeVO.setRedirectUrl(redirectUrl);
                codeVO.setUserName(userName);
                codeVO.setClientName(clientId);

                if (AuthorizeResponseType.CODE.name().equalsIgnoreCase(clientVO.getRespType())) {
                    // issue auth code
                    String code = oAuth2AuthCodeService.createAuthorizationCode(oAuth2Authentication);
                    codeVO.setCode(code);
                } else if (AuthorizeResponseType.TOKEN.name().equalsIgnoreCase(clientVO.getRespType())) {
                    // issue access token by implicit mode
                    OAuth2Authentication o2Authentication = oAuth2AuthCodeService.issueAuthentication(userName, null, clientId, scopes);
                    OAuth2AccessToken o2AccessToken = oAuth2AuthTokenService.createAccessToken(o2Authentication);
                    codeVO.setCode(o2AccessToken.getValue());
                }

            }
        }

        return codeVO;
    }

    @Override
    public OAuth2AccessToken issueToken(String code, String clientId, String clientSecret) {
        // check if the client own the auth code
        AuthorizationCodeEntity codeEntity = authCodeRepo.findByCode(code);

        if (codeEntity == null) {
            throw UnAuthorizeException.newException(MessageType.ERROR, "授权码无效：%s", code);
        }

        if (!clientId.equals(codeEntity.getClientId())) {
            throw new UnAuthorizeException(MessageType.ERROR, "Client does not own the presented auth code.");
        }

        // consume authorization code
        OAuth2Authentication o2Authentication = oAuth2AuthCodeService.consumeAuthorizationCode(code);

        // check the client id/secret
        ClientEntity client = clientRepo.findByIdEx(clientId);
        if (client == null || clientSecret == null || !clientSecret.equals(client.getClientSecret())) {
            throw new UnAuthorizeException(MessageType.ERROR, "Client不存在，或者Client Secret有误。");
        }

        // issue access/refresh token
        OAuth2AccessToken o2AccessToken = oAuth2AuthTokenService.createAccessToken(o2Authentication);
        return o2AccessToken;
    }

    @Override
    public OAuth2AccessToken issueToken(String username, String password, String clientId, String clientSecret) {
        Set<String> scopes = new HashSet<>();
        OAuth2Authentication o2Authentication = oAuth2AuthCodeService.issueAuthentication(username, password, clientId, scopes);

        // check the user name and password
        UserEntity userEntity = userService.checkUserAuthentication(username, password);
        if (userEntity == null) {
            throw new UnAuthorizeException(MessageType.ERROR, "用户不存在，或者用户登录信息有误。");
        }

        // check the client id/secret
        ClientEntity client = clientRepo.findByIdEx(clientId);
        if (client == null || clientSecret == null || !clientSecret.equals(client.getClientSecret())) {
            throw new UnAuthorizeException(MessageType.ERROR, "Client不存在，或者Client Secret有误。");
        }

        OAuth2AccessToken o2AccessToken = oAuth2AuthTokenService.createAccessToken(o2Authentication);
        return o2AccessToken;
    }


    @Override
    public OAuth2AccessToken issueToken(String clientId, String clientSecret) {
        // check the client id/secret
        ClientEntity client = clientRepo.findByIdEx(clientId);
        if (client == null || clientSecret == null || !clientSecret.equals(client.getClientSecret())) {
            throw new UnAuthorizeException(MessageType.ERROR, "Client不存在，或者Client Secret有误。");
        }

        Set<String> scopes = new HashSet<>();
        OAuth2Authentication o2Authentication = oAuth2AuthCodeService.issueAuthentication(null, null, clientId, scopes);

        OAuth2AccessToken o2AccessToken = oAuth2AuthTokenService.createAccessToken(o2Authentication);
        return o2AccessToken;
    }

    @Override
    public OAuth2AccessToken refreshToken(String token, String clientId, String clientSecret) {
        // check the client id/secret
        ClientEntity client = clientRepo.findByIdEx(clientId);
        if (client == null || clientSecret == null || !clientSecret.equals(client.getClientSecret())) {
            throw new UnAuthorizeException(MessageType.ERROR, "Client不存在，或者Client Secret有误。");
        }

        // refresh access token
        TokenRequest tokenRequest = new TokenRequest(null, clientId, null, null);
        OAuth2AccessToken o2AccessToken = oAuth2AuthTokenService.refreshAccessToken(token, tokenRequest);
        return o2AccessToken;
    }

    @Override
    public ValidityVO introspectToken(String token) {
        Object tokenEntity = null;
        tokenEntity = oAuth2AuthTokenService.getAccessToken(token);

        ValidityVO tokenValidity = new ValidityVO();
        tokenValidity.setIsValid(tokenEntity != null);
        return tokenValidity;
    }

    @Override
    public Boolean revokeToken(String token) {
        Boolean bSuccess = oAuth2AuthTokenService.revokeAccessToken(token);
        if (!bSuccess) {
            bSuccess = oAuth2AuthTokenService.revokeRefreshToken(token);
        }
        return bSuccess;
    }

}
