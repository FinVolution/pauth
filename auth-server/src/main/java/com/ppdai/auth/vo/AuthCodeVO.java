package com.ppdai.auth.vo;

import lombok.Data;

import java.util.Date;

/**
 * auth code view object
 *
 */
@Data
public class AuthCodeVO {

    Long id;
    String code;
    String redirectUrl;
    String clientName;
    String userName;
    Date expiration;
    Date insertTime;

}
