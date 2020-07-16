package com.ppdai.auth.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * auth token view object
 *
 */
@Data
public class AuthTokenVO {

    Long id;
    String value;
    String clientName;
    String userName;
    Date expiration;
    Date insertTime;

}
