package com.ppdai.auth.filter;

import com.ppdai.auth.common.identity.Identity;
import com.ppdai.auth.common.utils.JwtUtil;
import com.ppdai.auth.conf.TokenStoreType;
import com.ppdai.auth.utils.CookieUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 从token中获取用户信息
 *
 */
public class UserInfoFilter extends OncePerRequestFilter {

    private TokenStoreType tokenStoreType;
    private String tokenName;
    private String auditUserInfo;

    public UserInfoFilter(TokenStoreType tokenStoreType, String tokenName, String auditUserInfo) {
        this.tokenStoreType = tokenStoreType;
        this.tokenName = tokenName;
        this.auditUserInfo = auditUserInfo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = null;
        if (this.tokenStoreType == TokenStoreType.COOKIE) {
            Cookie cookie = CookieUtil.getCookieByName(httpRequest, this.tokenName);
            tokenValue = cookie != null ? cookie.getName() : null;
        } else if (this.tokenStoreType == TokenStoreType.HEADER) {
            tokenValue = httpRequest.getHeader(this.tokenName);
        }

        if (tokenValue != null && !tokenValue.equals("null")) {
            Identity identity = JwtUtil.decode(tokenValue);
            if (identity != null) {
                httpRequest.setAttribute(this.auditUserInfo, identity);
            }
        }

        filterChain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {

    }
}
