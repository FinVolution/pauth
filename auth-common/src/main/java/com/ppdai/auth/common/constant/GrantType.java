package com.ppdai.auth.common.constant;


/**
 * OAuth 2.0授权模式
 *
 */
public enum GrantType {

    /**
     * OAuth2授权码模式
     */
    AUTHORIZATION_CODE,

    /**
     * OAuth2刷新码
     */
    REFRESH_TOKEN,

    /**
     * OAuth2 Client Credential模式
     */
    CLIENT_CREDENTIALS,

    /**
     * OAuth2 Resource Owner Password模式
     */
    PASSWORD,

    /**
     * OAuth2简化模式
     */
    IMPLICIT,

}
