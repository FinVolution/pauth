package com.ppdai.auth.common.response;

/**
 * common web service response, which provide uniform format of response code and message
 *
 */
public class Response<T> {

    private Integer code;
    private String message;
    private T details;

    public static Response mark(MessageType type) {
        return mark(type, null);
    }

    public static <T> Response<T> mark(MessageType type, T details) {
        Response<T> response = new Response<>();
        response.code = type.getCode();
        response.message = type.getMsg();
        response.details = details;
        return response;
    }

    public static Response<String> mark(MessageType type, String message, Object... params){
        Response response;

        if (params != null && params.length > 0) {
            String formatMessage = String.format(message, params);
            response = Response.mark(type, formatMessage);
        } else {
            response = Response.mark(type, message);
        }

        return response;
    }

    public static <T> Response<T> success(T details) {
        Response<T> response = new Response<>();
        response.code = MessageType.SUCCESS.getCode();
        response.message = MessageType.SUCCESS.getMsg();
        response.details = details;
        return response;
    }

    public static <T> Response<T> error(String message, T details) {
        Response<T> response = new Response<>();
        response.code = MessageType.ERROR.getCode();
        response.message = message;
        response.details = details;
        return response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getDetails() {
        return details;
    }

    public void setDetails(T result) {
        this.details = result;
    }
}
