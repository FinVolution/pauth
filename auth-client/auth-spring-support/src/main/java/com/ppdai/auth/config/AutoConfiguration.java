package com.ppdai.auth.config;

import com.ppdai.auth.controller.PauthLoginController;
import com.ppdai.auth.controller.PauthTokenController;
import com.ppdai.auth.service.PauthLoginService;
import com.ppdai.auth.service.PauthTokenService;
import com.ppdai.auth.utils.PauthTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {
    @Bean
    public PauthLoginController pauthLoginController() {
        return new PauthLoginController();
    }

    @Bean
    public PauthTokenController pauthTokenController() {
        return new PauthTokenController();
    }

    @Bean
    public PauthLoginService pauthLoginService() {
        return new PauthLoginService();
    }

    @Bean
    public PauthTokenService pauthTokenService() {
        return new PauthTokenService();
    }

    @Bean
    public PauthTokenUtil pauthTokenUtil() {
        return new PauthTokenUtil();
    }
}
