package com.ppdai.auth.service;

import com.ppdai.auth.po.AccessTokenEntity;
import com.ppdai.auth.po.AuthorizationCodeEntity;
import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.po.RefreshTokenEntity;
import com.ppdai.auth.utils.ConvertUtil;
import com.ppdai.auth.vo.AuthCodeVO;
import com.ppdai.auth.vo.AuthTokenVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Please add description here.
 *
 */
@Service
public class MapService {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AuthHolderService authHolderService;

    public AuthCodeVO mapAuthCode(AuthorizationCodeEntity authCode) {
        AuthCodeVO authCodeVO = ConvertUtil.convert(authCode, AuthCodeVO.class);

        Long authHolderId = authCode.getAuthHolderId();
        String userName = authHolderService.getUserNameByHolderId(authHolderId);
        authCodeVO.setUserName(userName);

        String clientName = authHolderService.getClientNameByHolderId(authHolderId);
        authCodeVO.setClientName(clientName);

        ClientEntity client = clientService.findByClientName(clientName);
        if (client != null) {
            authCodeVO.setRedirectUrl(client.getRedirectUrl());
        }

        return authCodeVO;
    }

    public AuthTokenVO mapAccessToken(AccessTokenEntity accessToken) {
        String clientId = accessToken.getClientId();
        Long authHolderId = accessToken.getAuthHolderId();
        return build(accessToken, clientId, authHolderId);
    }

    public AuthTokenVO mapRefreshToken(RefreshTokenEntity refreshToken) {
        String clientId = refreshToken.getClientId();
        Long authHolderId = refreshToken.getAuthHolderId();

        return build(refreshToken, clientId, authHolderId);
    }

    private AuthTokenVO build(Object source, String clientId, Long authHolderId) {
        AuthTokenVO tokenVO = new AuthTokenVO();
        BeanUtils.copyProperties(source, tokenVO);
        if (clientId != null) {
            tokenVO.setClientName(clientId);
        }
        if (authHolderId != null) {
            tokenVO.setUserName(authHolderService.getUserNameByHolderId(authHolderId));
        }
        return tokenVO;
    }

}
