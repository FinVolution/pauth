package com.ppdai.auth.vo;

import com.ppdai.auth.common.constant.GrantType;
import lombok.Data;

/**
 * grant request view object
 *
 */
@Data
public class GrantRequestVO {

    GrantType grant_type;
    String code;
    String refresh_token;

    public GrantType getGrantType() {
        return grant_type;
    }

    public String getCode() {
        return code;
    }

    public String getRefreshToken() {
        return refresh_token;
    }
}
