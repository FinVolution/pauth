package com.ppdai.auth.exception;

import com.ppdai.auth.common.response.MessageType;

/**
 * a base exception
 *
 */
public class BaseException extends RuntimeException {

    private MessageType msgType = MessageType.UNKNOWN;

    public BaseException(MessageType msgType) {
        this.msgType = msgType;
    }

    public BaseException(MessageType msgType, Throwable cause) {
        super(cause);
        this.msgType = msgType;
    }

    public BaseException(MessageType msgType, String message) {
        super(message);
        this.msgType = msgType;
    }

    public BaseException(MessageType msgType, Throwable cause, String message) {
        super(message, cause);
        this.msgType = msgType;
    }

    public BaseException(MessageType msgType, String details, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(details, cause, enableSuppression, writableStackTrace);
        this.msgType = msgType;
    }

    public static BaseException newException(MessageType msgType, String message, Object... params) {
        BaseException exception;
        if (params != null && params.length > 0) {
            String formatMessage = String.format(message, params);
            if (params[params.length - 1] instanceof Throwable) {
                exception = new BaseException(msgType, (Throwable) params[params.length - 1], formatMessage);
            } else {
                exception = new BaseException(msgType, formatMessage);
            }
        } else {
            exception = new BaseException(msgType, message);
        }
        return exception;
    }

    public MessageType getMessageType() {
        return this.msgType;
    }

}
