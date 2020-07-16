package com.ppdai.auth.idp;

/**
 * identity provider's support class
 *
 */
public interface IdentityProvider {

    /**
     * support class for identity provider configuration
     *
     * @return support class for identity provider configuration
     */
    Class<? extends IdentityProviderConfiguration> configuration();

    /**
     * support class for authentication provider
     *
     * @return support class for authentication provider
     */
    Class<? extends AuthenticationProvider> authenticationProvider();

}
