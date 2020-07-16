package com.ppdai.auth.common.utils;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Please add description here.
 *
 */
public class BasicAuthUtil {

    public static String BASIC_PREFIX = "Basic";

    public static String[] decode(String authorization) {
        String[] values = null;

        if (authorization != null && authorization.startsWith(BASIC_PREFIX)) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring(BASIC_PREFIX.length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            // credentials = username:password
            values = credentials.split(":", 2);
        }
        return values;

    }

    public static String encode(String id, String secret) {
        String str = String.format("%s:%s", id, secret);
        str = Base64.getEncoder().encodeToString(str.getBytes());
        return String.format("%s %s", BASIC_PREFIX, str);
    }

}
