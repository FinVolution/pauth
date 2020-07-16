package com.ppdai.auth.aop;

import com.ppdai.auth.config.UserAuditorAware;
import com.ppdai.auth.service.UserService;
import com.ppdai.auth.utils.RequestContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
@Order(12)
public class AuthorizationAspect {

    @Autowired
    private UserService userService;

    @Before("ResourcePointCuts.apiController()")
    public void checkPermission(JoinPoint joinPoint) throws Throwable {
        // read user name from request attribute
        String userName = RequestContextUtil.getCurrentUserName();

        if (userName != null && !userName.equals(UserAuditorAware.DEFAULT_SYSTEM_NAME)) {
            // if the user name
            //    - doesn't exist in the database, add user into database
            //    - exit in the database, update the visit time
            if (!userService.hasUser(userName)) {
                log.info("add user into database");
                //authService.addUser(userName);
            } else {
                userService.updateLastVisitTime(userName);
            }
        }

        // TODO: check user's permission when user permission is ready
    }

}
