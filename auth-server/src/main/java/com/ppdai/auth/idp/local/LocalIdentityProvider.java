package com.ppdai.auth.idp.local;

import com.ppdai.auth.idp.AuthenticationProvider;
import com.ppdai.auth.idp.IdentityProvider;
import com.ppdai.auth.idp.IdentityProviderConfiguration;

/**
 * local identity provider
 *
 */
public class LocalIdentityProvider implements IdentityProvider {

    @Override
    public Class<? extends IdentityProviderConfiguration> configuration() {
        return LocalProviderConfiguration.class;
    }

    @Override
    public Class<? extends AuthenticationProvider> authenticationProvider() {
        return LocalAuthenticationProvider.class;
    }

}
