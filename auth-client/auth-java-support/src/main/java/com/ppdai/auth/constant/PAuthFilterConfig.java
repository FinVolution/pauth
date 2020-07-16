package com.ppdai.auth.constant;

public interface PAuthFilterConfig {
    /**
     * PAuth的api地址
     */
    String PAUTH_API_URL = "pauth.api.url";

    /**
     * PAuth filter的过滤类型，有ALL_CHECK_BY_SKIP和ALL_SKIP_BY_CHECK两个值可选
     */
    String PAUTH_JAVA_FILTER_TYPE = "pauth.java.filter.type";

    /**
     * 和过滤类型搭配使用的特殊url地址，如：GET&.*，多个地址之间用逗号分隔
     */
    String PAUTH_JAVA_FILTER_SPECIAL_URLS = "pauth.java.filter.special.urls";
}
