package com.ppdai.auth.exception;

import com.ppdai.auth.common.response.MessageType;

/**
 * a simple exception
 *
 */
public class SimpleException extends BaseException {


    public SimpleException(MessageType msgType) {
        super(msgType);
    }

    public SimpleException(MessageType msgType, Throwable cause) {
        super(msgType, cause);
    }

    public SimpleException(MessageType msgType, String message) {
        super(msgType, message);
    }

    public SimpleException(MessageType msgType, Throwable cause, String message) {
        super(msgType, cause, message);
    }

    public SimpleException(MessageType msgType, String details, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(msgType, details, cause, enableSuppression, writableStackTrace);
    }

    public static SimpleException newException(MessageType msgType, String message, Object... params) {
        SimpleException exception;
        if (params != null && params.length > 0) {
            String formatMessage = String.format(message, params);
            if (params[params.length - 1] instanceof Throwable) {
                exception = new SimpleException(msgType, (Throwable) params[params.length - 1], formatMessage);
            } else {
                exception = new SimpleException(msgType, formatMessage);
            }
        } else {
            exception = new SimpleException(msgType, message);
        }
        return exception;
    }

}
