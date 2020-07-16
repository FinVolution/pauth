package com.ppdai.auth.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Please add description here.
 *
 */
public class CookieUtil {

    final static Logger logger = LoggerFactory.getLogger(CookieUtil.class);

    /**
     * Print out the cookies from incoming request
     *
     * @param request the incoming request
     */
    static public void printCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            logger.info("No cookie.");
        } else {
            logger.info("print cookies on the request...");
            int count = 0;
            for (Cookie cookie : cookies) {
                logger.info(String.format("cookies[%s] name = %s, value = %s",
                        ++count, cookie.getName(), cookie.getValue()));
            }
        }

    }

    /**
     * set a cookie with default settings
     *
     * @param response the response to be send to user
     * @param name     cookie's name
     * @param value    cookie's value
     */
    static public void setCookie(HttpServletResponse response, String name, String value) {
        String defaultDomain = "localhost";
        String defaultPath = "/";
        boolean defaultSecure = false;
        int defaultExpiry = 3600;
        setCookie(response, name, value, defaultDomain, defaultPath, defaultSecure, defaultExpiry);
    }

    /**
     * set a cookie
     *
     * @param response the response to be send to user
     * @param name     cookie's name
     * @param value    cookie's value
     * @param expiry   cookie's expiration time (second)
     */
    static public void setCookie(HttpServletResponse response, String name, String value, int expiry) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(expiry);

        logger.info(String.format("add a cookie, name = %s, value = %s", cookie.getName(), cookie.getValue()));
        response.addCookie(cookie);
    }

    /**
     * set a cookie
     *
     * @param response the response to be send to user
     * @param name     cookie's name
     * @param value    cookie's value
     * @param domain   cookie's domain
     * @param path     cookie's path
     * @param isSecure is cookie for https only
     * @param expiry   cookie's expiration time (second)
     */
    static public void setCookie(HttpServletResponse response, String name, String value,
                                 String domain, String path, boolean isSecure, int expiry) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setMaxAge(expiry);
        cookie.setSecure(isSecure);

        logger.info(String.format("add a cookie, name = %s, value = %s", cookie.getName(), cookie.getValue()));
        response.addCookie(cookie);
    }

    /**
     * edit a cookie's value
     *
     * @param request  the incoming request
     * @param response the output response to be send to user
     * @param name     cookie's name
     * @param value    cookie's value
     * @return return a boolean value, which indicate if the cookies has been edited.
     */
    static public boolean editCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        boolean bHasEdited = false;
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    logger.info("cookie name:" + cookie.getName());
                    logger.info("cookie original value:" + cookie.getValue());
                    logger.info("cookie new value:" + value);
                    cookie.setValue(value);
                    response.addCookie(cookie);
                    bHasEdited = true;
                    break;
                }
            }
        }

        return bHasEdited;

    }

    /**
     * remove a cookie from response
     *
     * @param request  the incoming request
     * @param response the output response to be send to user
     * @param name     cookie's name
     * @return return a boolean value, which indicate if the cookies has been edited.
     */
    static public boolean removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        boolean bHasEdited = false;

        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            logger.info("No cookie.");
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue(null);
                    cookie.setPath("/");
                    // remove the cookie by setting the max age as 0 seconds
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    bHasEdited = true;
                    logger.info("Cookie is removed:" + cookie.getName());
                    break;
                }
            }
        }

        return bHasEdited;
    }

    /**
     * read the cookie by name
     *
     * @param request the incoming request
     * @param name    cookie's name
     * @return the cookie by name, or null if not found
     */
    static public Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = readCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * return the list of cookies by the request
     *
     * @param request the incoming request
     * @return a map of cookies
     */
    static public Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>(16);
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
}
