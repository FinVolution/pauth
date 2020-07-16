package com.ppdai.auth.oauth2;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.dao.AuthenticationHolderRepository;
import com.ppdai.auth.dao.AuthorizationCodeRepository;
import com.ppdai.auth.dao.ClientRepository;
import com.ppdai.auth.dao.UserRepository;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.exception.UnAuthorizeException;
import com.ppdai.auth.po.AuthenticationHolderEntity;
import com.ppdai.auth.po.AuthorizationCodeEntity;
import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.po.UserEntity;
import com.ppdai.auth.scheduler.OAuth2TokenExpireUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A implementation of authorization code service
 *
 */
@Service
public class OAuth2AuthCodeServiceImpl implements AuthorizationCodeServices {

    @Autowired
    private AuthenticationHolderRepository authHolderRepo;
    @Autowired
    private AuthorizationCodeRepository authCodeRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private OAuth2TokenExpireUtil tokenExpireUtil;

    private RandomValueStringGenerator generator;

    public OAuth2AuthCodeServiceImpl() {
        generator = new RandomValueStringGenerator();
    }

    @Override
    public String createAuthorizationCode(OAuth2Authentication o2Authentication) {
        String userName = (String) o2Authentication.getPrincipal();
        UserEntity user = userRepo.findOneByName(userName);

        // create authorization holder
        AuthenticationHolderEntity authHolder = new AuthenticationHolderEntity();
        authHolder.setAuthentication(o2Authentication);
        authHolder.setUserId(user.getId());

        String clientId = authHolder.getClientId();
        ClientEntity client = clientRepo.findByIdEx(clientId);
        if (client != null) {
            authHolder.setRedirectUri(client.getRedirectUrl());
        }
        authHolderRepo.save(authHolder);

        // issue authorization code
        String randomVal = generator.generate();
        AuthorizationCodeEntity codeEntity = new AuthorizationCodeEntity();
        codeEntity.setAuthHolderId(authHolder.getId());
        codeEntity.setCode(randomVal);
        Timestamp expiration = tokenExpireUtil.getOAuthCodeExpireTime();
        codeEntity.setExpiration(expiration);
        codeEntity.setUserId(user.getId());
        codeEntity.setClientId(clientId);
        authCodeRepo.save(codeEntity);

        return randomVal;
    }

    @Transactional(rollbackFor = BaseException.class)
    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        // remove any expired auth code
        tokenExpireUtil.removeAuthCode();

        // check the code
        AuthorizationCodeEntity codeEntity = authCodeRepo.findByCode(code);
        if (codeEntity == null) {
            throw UnAuthorizeException.newException(MessageType.ERROR, "授权码无效：%s", code);
        }

        // remove the auth code
        authCodeRepo.removeById(codeEntity.getId());

        Long authHolderId = codeEntity.getAuthHolderId();
        AuthenticationHolderEntity authHolder = authHolderRepo.findOne(authHolderId);

        String clientId = authHolder.getClientId();
        Long userId = authHolder.getUserId();
        UserEntity user = userRepo.findOne(userId);
        String userName = user.getName();

        Set<String> scopes = new HashSet<>();
        OAuth2Authentication oAuth2Authentication = issueAuthentication(userName, null, clientId, scopes);
        return oAuth2Authentication;
    }

    public OAuth2Authentication issueAuthentication(String userName, String password, String clientId, Set<String> scopes) {
        OAuth2Request oAuth2Request = new OAuth2Request(null, clientId, null, true, scopes, null, null, null, null);
        UsernamePasswordAuthenticationToken userAuthentication = null;
        if (userName != null) {
            userAuthentication = new UsernamePasswordAuthenticationToken(userName, password);
        }
        return new OAuth2Authentication(oAuth2Request, userAuthentication);
    }

    public OAuth2Authentication issueAuthentication(String userName, String clientId, Map<String, Serializable> extensionProperties) {
        OAuth2Request oAuth2Request = new OAuth2Request(null, clientId, null, true, null, null, null, null, extensionProperties);
        UsernamePasswordAuthenticationToken userAuthentication = null;
        if (userName != null) {
            userAuthentication = new UsernamePasswordAuthenticationToken(userName, null);
        }
        return new OAuth2Authentication(oAuth2Request, userAuthentication);
    }

}
