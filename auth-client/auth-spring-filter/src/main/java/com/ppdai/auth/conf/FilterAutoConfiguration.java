package com.ppdai.auth.conf;

import com.ppdai.auth.filter.PAuthSpringFilter;
import com.ppdai.auth.filter.UserInfoFilter;
import com.ppdai.pauth.client.api.OAuth2EndpointApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Please add description here.
 *
 */
@Configuration
public class FilterAutoConfiguration {

    final Logger logger = LoggerFactory.getLogger(FilterAutoConfiguration.class);

    @Value("${pauth.spring.filter.type:all-check-by-skip}")
    private String pauthFilterType;

    @Value("${pauth.spring.filter.token.store.type:header}")
    private String tokenStoreType;

    @Value("${pauth.spring.filter.token.name:pauth-token}")
    private String tokenName;

    @Value("${pauth.spring.filter.special.urls:}")
    private String specialUrls;

    @Value("${pauth.spring.filter.audit.userinfo:audit-userinfo}")
    private String auditUserInfo;

    @Bean
    @ConditionalOnProperty(name = "pauth.spring.filter.type", havingValue = "all-check-by-skip")
    public OncePerRequestFilter pAuthSpringFilter1(OAuth2EndpointApi pAuthApi) {
        return pAuthSpringFilter(pAuthApi, PAuthFilterType.ALL_CHECK_BY_SKIP);
    }

    @Bean
    @ConditionalOnProperty(name = "pauth.spring.filter.type", havingValue = "all-skip-by-check")
    public OncePerRequestFilter pAuthSpringFilter2(OAuth2EndpointApi pAuthApi) {
        return pAuthSpringFilter(pAuthApi, PAuthFilterType.ALL_SKIP_BY_CHECK);
    }

    @Bean
    @ConditionalOnProperty(name = "pauth.spring.filter.audit.userinfo")
    public OncePerRequestFilter userInfoFilter() {
        TokenStoreType storeType = parseTokenType();
        return new UserInfoFilter(storeType, this.tokenName, this.auditUserInfo);
    }

    private OncePerRequestFilter pAuthSpringFilter(OAuth2EndpointApi pAuthApi, PAuthFilterType pAuthFilterType) {
        TokenStoreType storeType = parseTokenType();
        return new PAuthSpringFilter(pAuthFilterType, storeType, tokenName, specialUrls, pAuthApi);
    }

    private TokenStoreType parseTokenType() {
        TokenStoreType storeType = TokenStoreType.COOKIE;

        try {
            storeType = TokenStoreType.valueOf(this.tokenStoreType.toUpperCase());
        } catch (IllegalArgumentException exception) {
            logger.info("The token store type is invalid, please look at valid type name in the enum class - TokenStoreType");
        }

        return storeType;
    }

}
