package com.ppdai.auth.vo;

import lombok.Data;

@Data
public class IntrospectVO {
    private ClientVO clientVO;
    private String username;
}
