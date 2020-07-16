package com.ppdai.auth.aop;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.exception.SimpleException;
import com.ppdai.auth.exception.UnAuthorizeException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Slf4j
@Order(11)
public class ExceptionAspect {

    @Around("ResourcePointCuts.webController()")
    public Object handleWebException(ProceedingJoinPoint apiMethod) {
        log.info("try to handle exception thrown from web controller method");

        Object retVal = null;
        try {
            retVal = apiMethod.proceed();
        } catch (BaseException e) {
            log.info(e.getMessage());
            retVal = Response.mark(e.getMessageType(), e.getMessage());
        } catch (Throwable throwable) {
            UUID uuid = UUID.randomUUID();
            String msg = String.format("[%s] %s", uuid, throwable.getMessage());
            log.error(msg, throwable);
            retVal = Response.mark(MessageType.UNKNOWN, "未知错误，请联系负责团队寻求更多帮助，定位GUID为[" + uuid + "]。");
        }

        return retVal;
    }

    @Around("ResourcePointCuts.oauth2Endpoint()")
    public Object handleOAuth2Exception(ProceedingJoinPoint apiMethod) {
        log.info("try to handle exception thrown from oauth2 controller method");

        Object retVal = null;
        try {
            retVal = apiMethod.proceed();
        } catch (UnAuthorizeException e) {
            log.info(e.getMessage());
            Response response = Response.mark(e.getMessageType(), e.getMessage());
            retVal = new ResponseEntity(response, HttpStatus.UNAUTHORIZED);
        } catch (SimpleException e) {
            log.info(e.getMessage());
            Response response = Response.mark(e.getMessageType(), e.getMessage());
            retVal = new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        } catch (Throwable throwable) {
            UUID uuid = UUID.randomUUID();
            String msg = String.format("[%s] %s", uuid, throwable.getMessage());
            log.error(msg, throwable);
            Response response = Response.mark(MessageType.UNKNOWN, "未知错误，请联系负责团队寻求更多帮助，定位GUID为[" + uuid + "]。");
            retVal = new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return retVal;
    }

}
