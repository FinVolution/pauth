package com.ppdai.auth.constant;

/**
 * Please add description here.
 *
 */
public enum PAuthFilterType {

    /**
     * 检查所有请求，跳过特定的URL
     */
    ALL_CHECK_BY_SKIP,

    /**
     * 跳过所有请求，检查特定的URL
     */
    ALL_SKIP_BY_CHECK,

}
