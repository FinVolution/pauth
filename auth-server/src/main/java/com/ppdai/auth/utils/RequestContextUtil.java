package com.ppdai.auth.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * a utils to read user name from request context
 *
 */
@Slf4j
public class RequestContextUtil {

    public static String getCurrentUserName() {
        String userName = null;

        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            userName = (String) requestAttributes.getAttribute(EnvProperty.HEADER_AUDIT_USERNAME, 0);
        } catch (Exception e) {
            log.info("Not able to read the user name by servlet requests. Probably it's a system call.");
        }

        return userName;
    }

}
