package com.ppdai.auth.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdai.auth.po.AuditLogEntity;
import com.ppdai.auth.service.AuditService;
import com.ppdai.auth.utils.RequestContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
@Order(10)
public class WebLogAspect {

    private final String GET_METHOD_NAME = "GET";

    @Autowired
    private AuditService auditService;

    @Before("ResourcePointCuts.apiController()")
    public void logAccessInfo(JoinPoint joinPoint) throws Throwable {
        log.info("logAccessInfo");
    }

    @Around("ResourcePointCuts.apiController()")
    public Object logAccessAudit(ProceedingJoinPoint apiMethod) throws Throwable {
        log.info("logAccessAudit");

        Object retVal = apiMethod.proceed();

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) attributes.getRequest();
            String httpMethod = request.getMethod();
            //Get请求以外的，以前认证方法以外的，需要记录日志。
            if(!GET_METHOD_NAME.equalsIgnoreCase(httpMethod) && !request.getRequestURI().contains("/authorize")){
                String userName = RequestContextUtil.getCurrentUserName();
                String httpUri = request.getRequestURI();
                String clientIp = request.getRemoteAddr();
                String classMethod = apiMethod.getSignature().getDeclaringTypeName() + "." + apiMethod.getSignature().getName();
                String classMethodArgs = Arrays.toString(apiMethod.getArgs());

                StringBuilder rsLog = new StringBuilder();
                rsLog.append("USER NAME: " + userName);
                rsLog.append(",HTTP_METHOD : " + httpMethod);
                rsLog.append(",HTTP_URI : " + httpUri);
                rsLog.append(",IP : " + clientIp);
                rsLog.append(",CLASS_METHOD : " + classMethod);
                rsLog.append(",CLASS_METHOD_ARGS : " + classMethodArgs);

                log.info(rsLog.toString());
                AuditLogEntity action = new AuditLogEntity();
                action.setUserName(userName);
                action.setHttpMethod(httpMethod);
                action.setHttpUri(httpUri);
                action.setClientIp(clientIp);
                action.setClassMethod(classMethod);
                action.setClassMethodArgs(classMethodArgs);

                ObjectMapper mapperObj = new ObjectMapper();
                String result = mapperObj.writeValueAsString(retVal);
                result = result.length() > 1024 ? result.substring(0, 1023) : result;
                action.setClassMethodReturn(result);

                auditService.recordOperation(action);
            }
        } catch (Exception e) {
            log.info("An exception happened when trying to log access info.");
        }

        return retVal;
    }


}
