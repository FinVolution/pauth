package com.ppdai.auth.vo;

import lombok.Data;

import java.util.Date;

/**
 * user view object
 *
 */
@Data
public class UserVO {
    Long id;
    String name;
    String email;
    Date lastVisitAt;
    Date insertTime;
}
