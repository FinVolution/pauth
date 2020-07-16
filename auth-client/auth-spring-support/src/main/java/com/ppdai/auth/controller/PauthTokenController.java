package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.pauth.client.ApiException;
import com.ppdai.pauth.client.api.OAuth2EndpointApi;
import com.ppdai.pauth.client.model.OAuth2AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/token")
public class PauthTokenController {

    final Logger log = LoggerFactory.getLogger(PauthTokenController.class);

    @Autowired
    private OAuth2EndpointApi pAuthApi;

    @Value("${pauth.api.authorization:}")
    private String authorization;

    @Value("${pauth.api.redirectUri:}")
    private String redirectUri;

    @RequestMapping(value = "/fetch", method = RequestMethod.GET)
    public Response fetchToken(@RequestParam(value = "code", required = true) String code) throws IOException {
        OAuth2AccessToken oAuth2AccessToken = null;

        if (code != null && this.authorization != null) {
            try {
                oAuth2AccessToken = pAuthApi.issueTokenUsingPOST("AUTHORIZATION_CODE", this.authorization, code, this.redirectUri, null, null, null);
            } catch (ApiException e) {
                log.warn("调用pAuthApi获取token发现异常，请确保参数正确以及code有效。err=" + e.getMessage(), e);
            }
        }

        if (oAuth2AccessToken != null) {
            return Response.mark(MessageType.SUCCESS, oAuth2AccessToken);
        } else {
            return Response.mark(MessageType.ERROR, "获取token失败，请确保参数正确以及code有效。");
        }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public Response refreshToken(@RequestParam(value = "refresh_token", required = true) String refreshToken) throws IOException {
        OAuth2AccessToken oAuth2AccessToken = null;

        if (refreshToken != null && this.authorization != null) {
            try {
                oAuth2AccessToken = pAuthApi.issueTokenUsingPOST("REFRESH_TOKEN", this.authorization, null, this.redirectUri, refreshToken, null, null);
            } catch (ApiException e) {
                log.warn("调用pAuthApi刷新token发现异常，请确保参数正确以及refreshToken有效。err=" + e.getMessage(), e);
            }
        }

        if (oAuth2AccessToken != null) {
            return Response.mark(MessageType.SUCCESS, oAuth2AccessToken);
        } else {
            return Response.mark(MessageType.ERROR, "刷新token失败，请确保参数正确以及refreshToken有效。");
        }
    }

    @RequestMapping(value = "/revoke", method = RequestMethod.GET)
    public Response revokeToken(@RequestParam(value = "token", required = true) String token) throws IOException {
        Boolean bSuccess = Boolean.FALSE;

        if (token != null && this.authorization != null) {
            try {
                bSuccess = pAuthApi.revokeTokenUsingPOST(token, this.authorization);
            } catch (ApiException e) {
                log.warn("调用pAuthApi吊销token发现异常，请确保参数以及token正确。err=" + e.getMessage(), e);
            }
        }

        if (bSuccess) {
            return Response.mark(MessageType.SUCCESS, "吊销token成功。");
        } else {
            return Response.mark(MessageType.ERROR, "吊销token失败，请确保参数以及token正确。");
        }
    }
}
