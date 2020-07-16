package com.ppdai.auth.config;

import com.ppdai.auth.idp.AuthenticationProvider;
import com.ppdai.auth.idp.atlas.AtlasAuthenticationProvider;
import com.ppdai.auth.idp.local.LocalAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityProviderConfig {

    @Bean("localIdpProvider")
    public AuthenticationProvider createLocalAuthenticationProvider() {
        return new LocalAuthenticationProvider();
    }

    @Bean("atlasLdpProvider")
    public AuthenticationProvider createAtlasAuthenticationProvider() {
        return new AtlasAuthenticationProvider();
    }

}
