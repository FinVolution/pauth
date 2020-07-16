package com.ppdai.auth.idp.atlas;

import com.ppdai.auth.idp.AuthenticationProvider;
import com.ppdai.auth.idp.IdentityProvider;
import com.ppdai.auth.idp.IdentityProviderConfiguration;

/**
 * atlas identity provider
 *
 */
public class AtlasIdentityProvider implements IdentityProvider {

    @Override
    public Class<? extends IdentityProviderConfiguration> configuration() {
        return AtlasProviderConfiguration.class;
    }

    @Override
    public Class<? extends AuthenticationProvider> authenticationProvider() {
        return AtlasAuthenticationProvider.class;
    }

}
