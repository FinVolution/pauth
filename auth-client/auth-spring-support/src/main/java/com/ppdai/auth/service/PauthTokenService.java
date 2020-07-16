package com.ppdai.auth.service;

import com.ppdai.auth.utils.CookieUtil;
import com.ppdai.pauth.client.ApiException;
import com.ppdai.pauth.client.api.OAuth2EndpointApi;
import com.ppdai.pauth.client.model.OAuth2AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Service
public class PauthTokenService {

    final Logger log = LoggerFactory.getLogger(PauthTokenService.class);

    @Autowired
    private OAuth2EndpointApi pAuthApi;

    @Value("${pauth.api.authorization:}")
    private String authorization;

    @Value("${pauth.api.redirectUri:}")
    private String redirectUri;

    @Value("${pauth.spring.filter.token.name:pauth-token}")
    private String tokenName;

    public OAuth2AccessToken fetchToken(String code) {
        OAuth2AccessToken oAuth2AccessToken = null;

        if (code != null && this.authorization != null) {
            try {
                oAuth2AccessToken = pAuthApi.issueTokenUsingPOST("AUTHORIZATION_CODE", this.authorization, code, this.redirectUri, null, null, null);
            } catch (ApiException e) {
                log.warn("调用pAuthApi获取token发现异常，请确保参数正确以及code有效。err=" + e.getMessage(), e);
            }
        }

        return oAuth2AccessToken;
    }

    public Boolean fetchToken(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AccessToken oAuth2AccessToken = null;
        String code = request.getParameter("code");
        if (code != null && this.authorization != null) {
            try {
                oAuth2AccessToken = pAuthApi.issueTokenUsingPOST("AUTHORIZATION_CODE", this.authorization, code, this.redirectUri, null, null, null);
            } catch (ApiException e) {
                log.warn("调用pAuthApi获取token发现异常，请确保参数正确以及code有效。err=" + e.getMessage(), e);
            }
        }

        if (oAuth2AccessToken != null) {
            CookieUtil.setCookie(response, this.tokenName, oAuth2AccessToken.getAccessToken(), 6 * 60 * 60);
            CookieUtil.setCookie(response, "refreshToken", oAuth2AccessToken.getRefreshToken(), 3 * 24 * 60 * 60);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean fetchToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();

            OAuth2AccessToken oAuth2AccessToken = null;
            String code = request.getParameter("code");
            if (code != null && this.authorization != null) {
                oAuth2AccessToken = pAuthApi.issueTokenUsingPOST("AUTHORIZATION_CODE", this.authorization, code, this.redirectUri, null, null, null);
            }

            if (oAuth2AccessToken != null) {
                CookieUtil.setCookie(response, this.tokenName, oAuth2AccessToken.getAccessToken(), 6 * 60 * 60);
                CookieUtil.setCookie(response, "refreshToken", oAuth2AccessToken.getRefreshToken(), 3 * 24 * 60 * 60);
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (ApiException e) {
            String msg = "调用pAuthApi获取token发现异常，请确保参数正确以及code有效。err=" + e.getMessage();
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (Exception e) {
            String msg = "获取token失败。err=" + e.getMessage();
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public OAuth2AccessToken refreshToken(String refreshToken) {
        OAuth2AccessToken oAuth2AccessToken = null;

        if (refreshToken != null && this.authorization != null) {
            try {
                oAuth2AccessToken = pAuthApi.issueTokenUsingPOST("REFRESH_TOKEN", this.authorization, null, this.redirectUri, refreshToken, null, null);
            } catch (ApiException e) {
                log.warn("调用pAuthApi刷新token发现异常，请确保参数正确以及refreshToken有效。err=" + e.getMessage(), e);
            }
        }

        return oAuth2AccessToken;
    }

    public Boolean refreshToken(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AccessToken oAuth2AccessToken = null;

        Cookie cookie = CookieUtil.getCookieByName(request, "refreshToken");

        if (cookie != null && cookie.getValue() != null && this.authorization != null) {
            try {
                oAuth2AccessToken = pAuthApi.issueTokenUsingPOST("REFRESH_TOKEN", this.authorization, null, this.redirectUri, cookie.getValue(), null, null);
            } catch (ApiException e) {
                log.warn("调用pAuthApi刷新token发现异常，请确保参数正确以及refreshToken有效。err=" + e.getMessage(), e);
            }
        }

        if (oAuth2AccessToken != null) {
            CookieUtil.setCookie(response, this.tokenName, oAuth2AccessToken.getAccessToken(), 6 * 60 * 60);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean refreshToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();

            OAuth2AccessToken oAuth2AccessToken = null;

            Cookie cookie = CookieUtil.getCookieByName(request, "refreshToken");

            if (cookie != null && cookie.getValue() != null && this.authorization != null) {
                oAuth2AccessToken = pAuthApi.issueTokenUsingPOST("REFRESH_TOKEN", this.authorization, null, this.redirectUri, cookie.getValue(), null, null);
            }

            if (oAuth2AccessToken != null) {
                CookieUtil.setCookie(response, this.tokenName, oAuth2AccessToken.getAccessToken(), 6 * 60 * 60);
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (ApiException e) {
            String msg = "调用pAuthApi刷新token发现异常，请确保参数正确以及refreshToken有效。err=" + e.getMessage();
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (Exception e) {
            String msg = "刷新token失败。err=" + e.getMessage();
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public Boolean revokeToken(String token) {
        Boolean bSuccess = Boolean.FALSE;

        if (token != null && this.authorization != null) {
            try {
                bSuccess = pAuthApi.revokeTokenUsingPOST(token, this.authorization);
            } catch (ApiException e) {
                log.warn("调用pAuthApi吊销token发现异常，请确保参数以及token正确。err=" + e.getMessage(), e);
            }
        }

        return bSuccess;
    }

    public Boolean revokeToken(HttpServletRequest request, HttpServletResponse response) {
        Boolean bSuccess = Boolean.FALSE;

        Cookie cookie = CookieUtil.getCookieByName(request, this.tokenName);

        if (cookie != null && cookie.getValue() != null && this.authorization != null) {
            try {
                bSuccess = pAuthApi.revokeTokenUsingPOST(cookie.getValue(), this.authorization);
            } catch (ApiException e) {
                log.warn("调用pAuthApi吊销token发现异常，请确保参数以及token正确。err=" + e.getMessage(), e);
            }
        }

        CookieUtil.setCookie(response, this.tokenName, null, 0);
        CookieUtil.setCookie(response, "refreshToken", null, 0);

        return bSuccess;
    }

    public Boolean revokeToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();

            Boolean bSuccess = Boolean.FALSE;

            Cookie cookie = CookieUtil.getCookieByName(request, this.tokenName);

            if (cookie != null && cookie.getValue() != null && this.authorization != null) {
                bSuccess = pAuthApi.revokeTokenUsingPOST(cookie.getValue(), this.authorization);
            }

            CookieUtil.setCookie(response, this.tokenName, null, 0);
            CookieUtil.setCookie(response, "refreshToken", null, 0);

            return bSuccess;
        } catch (ApiException e) {
            String msg = "调用pAuthApi吊销token发现异常，请确保参数以及token正确。err=" + e.getMessage();
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (Exception e) {
            String msg = "吊销token失败。err=" + e.getMessage();
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
