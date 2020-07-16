package com.ppdai.auth.utils;

import com.ppdai.auth.common.identity.DefaultIdentity;
import com.ppdai.auth.common.identity.Identity;
import com.ppdai.auth.common.utils.JwtUtil;
import com.ppdai.auth.conf.TokenStoreType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class PauthTokenUtil {

    @Value("${pauth.spring.filter.token.store.type:header}")
    private String tokenStoreType;

    @Value("${pauth.spring.filter.token.name:pauth-token}")
    private String tokenName;

    public Identity getTokenInfo(HttpServletRequest request) {
        Identity identity = new DefaultIdentity(null);
        String token = null;
        TokenStoreType storeType = TokenStoreType.valueOf(this.tokenStoreType.toUpperCase());

        if (storeType == TokenStoreType.HEADER) {
            token = request.getHeader(this.tokenName);
        } else if (storeType == TokenStoreType.COOKIE) {
            Cookie cookie = CookieUtil.getCookieByName(request, this.tokenName);
            if (cookie != null) {
                token = cookie.getValue();
            }
        }

        if (token != null && !token.equals("null")) {
            identity = JwtUtil.decode(token);
        }
        return identity;
    }

    public Identity getTokenInfo() {
        Identity identity = new DefaultIdentity(null);
        String token = getToken();

        if (token != null && !token.equals("null")) {
            identity = JwtUtil.decode(token);
        }

        return identity;
    }

    public String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String token = null;
        TokenStoreType storeType = TokenStoreType.valueOf(this.tokenStoreType.toUpperCase());

        if (storeType == TokenStoreType.HEADER) {
            token = request.getHeader(this.tokenName);
        } else if (storeType == TokenStoreType.COOKIE) {
            Cookie cookie = CookieUtil.getCookieByName(request, this.tokenName);
            if (cookie != null) {
                token = cookie.getValue();
            }
        }

        return token;
    }

}
