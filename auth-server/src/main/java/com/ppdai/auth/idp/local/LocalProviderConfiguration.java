package com.ppdai.auth.idp.local;

import com.ppdai.auth.idp.IdentityProviderConfiguration;

/**
 * the required configuration of local identity provider
 *
 */
public class LocalProviderConfiguration implements IdentityProviderConfiguration {

    @Override
    public Class<? extends IdentityProviderConfiguration> support() {
        return this.getClass();
    }

}
