package com.ppdai.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ppdai.auth.common.identity.DefaultIdentity;
import com.ppdai.auth.common.identity.Identity;
import com.ppdai.auth.common.utils.JwtUtil;
import com.ppdai.auth.utils.CookieUtil;
import com.ppdai.pauth.client.ApiException;
import com.ppdai.pauth.client.api.OAuth2EndpointApi;
import com.ppdai.pauth.client.model.OAuth2AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PAuthClient {
    final Logger log = LoggerFactory.getLogger(PAuthClient.class);

    private OAuth2EndpointApi pAuthApi;
    private String pAuthApiUrl;
    private String clientId;
    private String redirectUri;
    private String authorization;

    /**
     *
     * @param pAuthApiUrl PAuth的api地址
     * @param clientId PAuth上注册的Client名
     * @param redirectUri Client用来接收PAuth授权码的回调地址
     * @param authorization 注册Client时生成的Basic字符串
     */
    public PAuthClient(String pAuthApiUrl, String clientId, String redirectUri, String authorization) {
        this.pAuthApi = new OAuth2EndpointApi();
        this.pAuthApiUrl = pAuthApiUrl;
        this.pAuthApi.getApiClient().setBasePath(this.pAuthApiUrl);
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.authorization = authorization;
    }

    public String fetchLoginUrl() {
        String loginUrl = null;
        String redirect_uri = this.redirectUri;

        if (this.pAuthApiUrl != null && redirect_uri != null && this.clientId != null) {
            try {
                redirect_uri = URLEncoder.encode(redirect_uri, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.error("redirectUri编码失败，编码类型不支持。err=" + e.getMessage(), e);
            }
            loginUrl = this.pAuthApiUrl + "/#/authorize?response_type=code&client_id=" + this.clientId + "&redirect_uri=" + redirect_uri + "&scope=user_role&state=1";
        }

        return loginUrl;
    }

    public OAuth2AccessToken fetchToken(String code) {
        OAuth2AccessToken oAuth2AccessToken = null;

        if (code != null && this.authorization != null) {
            try {
                oAuth2AccessToken = this.pAuthApi.issueTokenUsingPOST("AUTHORIZATION_CODE", this.authorization, code, this.redirectUri, null, null, null);
            } catch (ApiException e) {
                log.error("调用pAuthApi获取token发现异常，请确保参数正确以及code有效。err=" + e.getMessage(), e);
            }
        }

        return oAuth2AccessToken;
    }

    public Boolean fetchToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            OAuth2AccessToken oAuth2AccessToken = null;
            String code = request.getParameter("code");
            if (code != null && this.authorization != null) {
                oAuth2AccessToken = this.pAuthApi.issueTokenUsingPOST("AUTHORIZATION_CODE", this.authorization, code, this.redirectUri, null, null, null);
            }

            if (oAuth2AccessToken != null) {
                long accessTokenActiveTime = this.getTokenActiveTime(oAuth2AccessToken.getAccessToken());
                long refreshTokenActiveTime = this.getTokenActiveTime(oAuth2AccessToken.getRefreshToken());

                CookieUtil.setCookie(response, "accessToken", oAuth2AccessToken.getAccessToken(), (int) accessTokenActiveTime);
                CookieUtil.setCookie(response, "refreshToken", oAuth2AccessToken.getRefreshToken(), (int) refreshTokenActiveTime);

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
                oAuth2AccessToken = this.pAuthApi.issueTokenUsingPOST("REFRESH_TOKEN", this.authorization, null, this.redirectUri, refreshToken, null, null);
            } catch (ApiException e) {
                log.error("调用pAuthApi刷新token发现异常，请确保参数正确以及refreshToken有效。err=" + e.getMessage(), e);
            }
        }

        return oAuth2AccessToken;
    }

    public Boolean refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            OAuth2AccessToken oAuth2AccessToken = null;

            Cookie cookie = CookieUtil.getCookieByName(request, "refreshToken");

            if (cookie != null && cookie.getValue() != null && this.authorization != null) {
                oAuth2AccessToken = this.pAuthApi.issueTokenUsingPOST("REFRESH_TOKEN", this.authorization, null, this.redirectUri, cookie.getValue(), null, null);
            }

            if (oAuth2AccessToken != null) {
                long accessTokenActiveTime = this.getTokenActiveTime(oAuth2AccessToken.getAccessToken());
                CookieUtil.setCookie(response, "accessToken", oAuth2AccessToken.getAccessToken(), (int) accessTokenActiveTime);

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
                bSuccess = this.pAuthApi.revokeTokenUsingPOST(token, this.authorization);
            } catch (ApiException e) {
                log.error("调用pAuthApi吊销token发现异常，请确保参数以及token正确。err=" + e.getMessage(), e);
            }
        }

        return bSuccess;
    }

    public Boolean revokeToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            Boolean bSuccess = Boolean.FALSE;

            Cookie cookie = CookieUtil.getCookieByName(request, "accessToken");

            if (cookie != null && cookie.getValue() != null && this.authorization != null) {
                bSuccess = this.pAuthApi.revokeTokenUsingPOST(cookie.getValue(), this.authorization);
            }

            CookieUtil.setCookie(response, "accessToken", null, 0);
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

    public Identity getTokenInfo(HttpServletRequest request) {
        Identity identity = new DefaultIdentity(null);

        String token = this.getToken(request);
        if (token != null && !token.equals("null")) {
            identity = JwtUtil.decode(token);
        }

        return identity;
    }

    public String getToken(HttpServletRequest request) {
        String token = null;

        Cookie cookie = CookieUtil.getCookieByName(request, "accessToken");
        if (cookie != null) {
            token = cookie.getValue();
        }

        return token;
    }

    public long getTokenActiveTime(String token) {
        DecodedJWT jwt = JWT.decode(token);

        long endTime = jwt.getClaim("exp").as(Long.class);
        long startTime = jwt.getClaim("iat").as(Long.class);

        return endTime - startTime;
    }
}
