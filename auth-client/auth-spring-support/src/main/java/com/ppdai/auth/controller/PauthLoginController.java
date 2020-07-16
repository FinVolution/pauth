package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api/login")
public class PauthLoginController {

    final Logger log = LoggerFactory.getLogger(PauthTokenController.class);

    @Value("${pauth.api.url:}")
    private String pauthUrl;

    @Value("${pauth.api.redirectUri:}")
    private String redirectUri;

    @Value("${pauth.api.clientId:}")
    private String clientId;

    @RequestMapping(value = "/url", method = RequestMethod.GET)
    public Response fetchLoginUrl(@RequestParam(value = "redirect_url", required = false) String redirectUrl) {
        String redirect_uri = redirectUrl == null ? redirectUri : redirectUrl;

        if (pauthUrl != null && redirect_uri != null && clientId != null) {
            try {
                redirect_uri = URLEncoder.encode(redirect_uri, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.info(e.getMessage(), e);
            }
            String loginUrl = pauthUrl + "/#/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirect_uri + "&scope=user_role&state=1";
            return Response.mark(MessageType.SUCCESS, loginUrl);
        } else {
            return Response.mark(MessageType.ERROR, "获取登录地址失败，请检查配置项是否完整。");
        }
    }
}
