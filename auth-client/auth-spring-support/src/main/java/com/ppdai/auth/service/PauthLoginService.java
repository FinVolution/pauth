package com.ppdai.auth.service;

import com.ppdai.auth.controller.PauthTokenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class PauthLoginService {
    final Logger log = LoggerFactory.getLogger(PauthTokenController.class);

    @Value("${pauth.api.url:}")
    private String pauthUrl;

    @Value("${pauth.api.redirectUri:}")
    private String redirectUri;

    @Value("${pauth.api.clientId:}")
    private String clientId;

    public String fetchLoginUrl() {
        String loginUrl = null;
        String redirect_uri = redirectUri;

        if (pauthUrl != null && redirect_uri != null && clientId != null) {
            try {
                redirect_uri = URLEncoder.encode(redirect_uri, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.info(e.getMessage(), e);
            }
            loginUrl = pauthUrl + "/#/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirect_uri + "&scope=user_role&state=1";
        }

        return loginUrl;
    }
}
