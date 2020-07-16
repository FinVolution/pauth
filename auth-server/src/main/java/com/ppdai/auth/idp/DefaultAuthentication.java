package com.ppdai.auth.idp;

/**
 * Default implementation of identity authentication
 *
 */
public class DefaultAuthentication implements Authentication {

    private String userName;
    private String userPwd;

    public DefaultAuthentication(String userName, String userPwd) {
        this.userName = userName;
        this.userPwd = userPwd;
    }

    @Override
    public Object getCredentials() {
        return this.userPwd;
    }

    @Override
    public Object getPrincipal() {
        return this.userName;
    }
}
