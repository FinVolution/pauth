package com.ppdai.auth.idp;

/**
 * a authentication which stores the identity's security info
 *
 */
public interface Authentication {

    /**
     * identity's credential
     *
     * @return credential
     */
    Object getCredentials();

    /**
     * identity's principal info
     *
     * @return principal
     */
    Object getPrincipal();

}
