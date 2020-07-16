package com.ppdai.auth.common.response;

/**
 * common web service response, which provide uniform format of response code and message
 *
 */
public enum MessageType {

    /**
     * 请求成功消息
     */
    SUCCESS(0, "请求成功完成。"),

    /**
     * 请求中发现错误
     */
    ERROR(-1, "发现错误。"),

    /**
     * 请求中发现未知错误
     */
    UNKNOWN(-4, "未知错误。");

    private Integer code;
    private String msg;

    MessageType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}
