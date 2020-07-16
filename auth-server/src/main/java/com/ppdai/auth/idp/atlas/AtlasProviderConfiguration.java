package com.ppdai.auth.idp.atlas;

import com.ppdai.auth.idp.IdentityProviderConfiguration;

/**
 * the required configuration of atlas identity provider
 *
 */
public class AtlasProviderConfiguration implements IdentityProviderConfiguration {

    @Override
    public Class<? extends IdentityProviderConfiguration> support() {
        return this.getClass();
    }

}
