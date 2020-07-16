package com.ppdai.auth.conf;

import com.ppdai.pauth.client.api.OAuth2EndpointApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 实现pauth client的spring自动配置
 *
 */
public class ClientAutoConfiguration {

    @Value("${pauth.api.url:http://localhost:8090}")
    private String pauthApiUrl;

    @Bean
    @ConditionalOnProperty(name = "pauth.api.url")
    public OAuth2EndpointApi oAuth2EndpointApi() {
        OAuth2EndpointApi oAuth2EndpointApi = new OAuth2EndpointApi();
        oAuth2EndpointApi.getApiClient().setBasePath(pauthApiUrl);
        return oAuth2EndpointApi;
    }

}
