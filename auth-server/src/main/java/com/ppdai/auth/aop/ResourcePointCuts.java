package com.ppdai.auth.aop;

import org.aspectj.lang.annotation.Pointcut;

public class ResourcePointCuts {

    /**
     * all rest api, which includes web controller and oauth2 endpoint
     */
    @Pointcut("execution(public * com.ppdai.auth.controller..*.*(..))")
    public void apiController() {
    }

    /**
     * web controller
     */
    @Pointcut("execution(public * com.ppdai.auth.controller..*Controller.*(..))")
    public void webController() {
    }

    /**
     * oauth2 endpoint
     */
    @Pointcut("execution(public * com.ppdai.auth.controller..*Endpoint.*(..))")
    public void oauth2Endpoint() {
    }

}
