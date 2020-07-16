package com.ppdai.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.auth.constant.PAuthFilterConfig;
import com.ppdai.auth.constant.PAuthFilterType;
import com.ppdai.auth.utils.CookieUtil;
import com.ppdai.pauth.client.ApiException;
import com.ppdai.pauth.client.api.OAuth2EndpointApi;
import com.ppdai.pauth.client.model.ValidityVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PAuthJavaFilter implements Filter {
    final Logger logger = LoggerFactory.getLogger(PAuthJavaFilter.class);

    private OAuth2EndpointApi pAuthApi;
    private String pAuthApiUrl;
    private PAuthFilterType pAuthFilterType;
    private String specialUrls;

    @Override
    public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
        this.pAuthApi = new OAuth2EndpointApi();
        this.pAuthApiUrl = filterConfig.getInitParameter(PAuthFilterConfig.PAUTH_API_URL);
        this.pAuthApi.getApiClient().setBasePath(this.pAuthApiUrl);
        this.pAuthFilterType = PAuthFilterType.valueOf(filterConfig.getInitParameter(PAuthFilterConfig.PAUTH_JAVA_FILTER_TYPE).toUpperCase());
        this.specialUrls = filterConfig.getInitParameter(PAuthFilterConfig.PAUTH_JAVA_FILTER_SPECIAL_URLS);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String httpUri = request.getRequestURI();
        String httpMethod = request.getMethod();

        if (this.pAuthFilterType == PAuthFilterType.ALL_CHECK_BY_SKIP) {
            // 检查所有请求，跳过特定的URL
            if (isUriSpecial(httpUri, httpMethod)) {
                // 跳过特定的URL
                chain.doFilter(request, response);
            } else {
                doIntrospect(request, response, chain);
            }

        } else if (this.pAuthFilterType == PAuthFilterType.ALL_SKIP_BY_CHECK) {
            // 跳过所有请求，检查特定的URL
            if (isUriSpecial(httpUri, httpMethod)) {
                // 检查特定的URL
                doIntrospect(request, response, chain);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    private void doIntrospect(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Boolean isTokenValid = Boolean.FALSE;

        // 从cookie中获取jwt token value
        Cookie cookie = CookieUtil.getCookieByName(request, "accessToken");
        String tokenValue = (cookie != null) ? cookie.getValue() : null;

        // 校验token的有效性
        if (tokenValue != null) {
            try {
                ValidityVO validityVO = this.pAuthApi.introspectTokenUsingPOST(tokenValue);
                isTokenValid = validityVO.getIsValid();
            } catch (ApiException e) {
                logger.info("Receive an exception when trying to introspect the token by pauth client.", e.getMessage());
            }
        }

        // 如果token有效，则继续请求，否则返回SC_UNAUTHORIZED错误的响应
        if (isTokenValid) {
            chain.doFilter(request, response);
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");

            String message;
            if (tokenValue == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                message = "accessToken is null, please login first.";
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                message = "accessToken is invalid, please login again.";
            }

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(message));
        }
    }

    private Boolean isUriSpecial(String urlTarget, String httpMethod) {
        Boolean isSpecial = false;

        String[] uris = this.specialUrls.split(",");
        for (String uri : uris) {
            uri = uri.trim();

            // isMatchByEquals 是否完全匹配
            // isMatchByRegExp 是否正则匹配
            // isMatchByHttpMethod 是否请求方法相匹配，默认为true
            boolean isMatchByEquals = false;
            boolean isMatchByRegExp = false;
            boolean isMatchByHttpMethod = true;

            // 检查uri是否以http方法开头
            String httpMethodRegex = "^(GET|POST|PUT|DELETE|OPTIONS|HEAD|TRACE|CONNECT|get|post|put|delete|options|head|trace|connect)&.*";
            boolean containsMethod = Pattern.matches(httpMethodRegex, uri);

            // 若uri包含http方法，则检查请求方法，并且获取指定的uriSkip
            // 样例 GET&^/api.*，匹配以/api开头的所有GET请求
            if (containsMethod) {
                String[] uriArray = uri.split("&", 2);
                isMatchByHttpMethod = httpMethod.equals(uriArray[0].toUpperCase());
                uri = uriArray[1];
            }

            isMatchByEquals = urlTarget.equals(uri);
            try {
                isMatchByRegExp = Pattern.matches(uri, urlTarget);
            } catch (PatternSyntaxException e) {
                logger.info("A syntax exception exists in the uri: " + uri);
            }

            // if the target url matches any of Uri, break the loop
            // otherwise continue the check with next Uri
            isSpecial = (isMatchByEquals || isMatchByRegExp) && isMatchByHttpMethod;
            if (isSpecial) {
                break;
            }
        }

        return isSpecial;
    }

    @Override
    public void destroy() {

    }
}
