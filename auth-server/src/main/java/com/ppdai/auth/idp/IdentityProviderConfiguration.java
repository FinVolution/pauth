package com.ppdai.auth.idp;

/**
 * identity provider configuration
 *
 */
public interface IdentityProviderConfiguration {

    /**
     * support class of identity provider configuration
     * @return support class of identity provider configuration
     */
    Class<? extends IdentityProviderConfiguration> support();

}
