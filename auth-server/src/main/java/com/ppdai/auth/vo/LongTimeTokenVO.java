package com.ppdai.auth.vo;

import lombok.Data;

@Data
public class LongTimeTokenVO {
    private String userName;
    private String clientId;
    private Long lifeCycle;
}
