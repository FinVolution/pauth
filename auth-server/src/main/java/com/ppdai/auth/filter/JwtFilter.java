package com.ppdai.auth.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.utils.EnvProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * a request filter to check security jwt token
 *
 */
@Component
@Slf4j
public class JwtFilter implements Filter {

    @Autowired
    private EnvProperty envProperty;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String httpMethod = httpRequest.getMethod();
        String httpUri = httpRequest.getRequestURI();

        if (!envProperty.JWT_CHECK_ENABLE || checkUriSkip(httpUri) || httpMethod.equals("GET")) {
            // skip jwt check
            chain.doFilter(request, response);
        } else {
            try {
                doAuthentication(request);
                chain.doFilter(request, response);
            } catch (BaseException e) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.setContentType("application/json; charset=utf-8");
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

                Response result = Response.mark(e.getMessageType(), e.getMessage());
                ObjectMapper mapper = new ObjectMapper();
                httpResponse.getWriter().write(mapper.writeValueAsString(result));
            }
        }
    }

    @Override
    public void destroy() {
    }

    private Boolean checkUriSkip(String uriToCheck) {
        Boolean isSkip = false;

        String[] uris = envProperty.JWT_CHECK_SKIP_URI.split(",");
        for (String uri : uris) {
            uri = uri.trim();
            if (uriToCheck.equals(uri)) {
                isSkip = true;
                break;
            }
        }

        return isSkip;
    }

    private void doAuthentication(ServletRequest request) throws BaseException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String jwtToken = httpRequest.getHeader("jwt-token");
        if (jwtToken == null) {
            throw BaseException.newException(MessageType.ERROR, "用户令牌为空，请登录系统后进行操作。");
        }

        String userName = null;
        try {
            JWT jwt = decodeToken(jwtToken);
            userName = jwt.getSubject();
        } catch (Exception ex) {
            log.info("failed to decode the user info from jwt token: " + jwtToken);
        }

        if (userName == null || userName.isEmpty()) {
            throw BaseException.newException(MessageType.ERROR, "用户令牌解析失败，无法确认当前操作的用户，请尝试重新登录。");
        }

        // set the user name into request attribute, which will be used by Spring JPA　Auditor　Aware
        httpRequest.setAttribute(EnvProperty.HEADER_AUDIT_USERNAME, userName);
    }

    private JWT decodeToken(String jwtToken) {
        JWT jwt = null;

        try {
            // verify jwt signature
            JWT.require(Algorithm.HMAC256(envProperty.JWT_SIGN_SECRET))
                    .withIssuer(envProperty.JWT_SIGN_ISSUER)
                    .build()
                    .verify(jwtToken);

            // decode jwt token
            jwt = JWT.decode(jwtToken);
        } catch (JWTVerificationException e) {
            log.warn("invalid jwt sign is found.");
        } catch (UnsupportedEncodingException e) {
            log.warn("unsuppoed jwt encodking is found.");
        }

        return jwt;
    }

}
