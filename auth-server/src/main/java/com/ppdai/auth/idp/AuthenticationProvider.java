package com.ppdai.auth.idp;

import com.ppdai.auth.common.identity.Identity;

/**
 * The authentication provider, which helps to load identity by id/authentication
 *
 */
public interface AuthenticationProvider {

    /**
     * load identity by its id
     *
     * @param id identity id
     * @return identity object, return null if not found
     */
    Identity load(String id);

    /**
     * load identity by its authentication
     *
     * @param authentication identity authentication
     * @return identity object, return null if not found
     */
    Identity load(Authentication authentication);

    /**
     * the provider class supported
     *
     * @return supported class
     */
    Class<? extends AuthenticationProvider> support();

}
