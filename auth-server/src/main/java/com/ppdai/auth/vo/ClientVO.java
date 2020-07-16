package com.ppdai.auth.vo;


import lombok.Data;

import java.util.Set;

/**
 * client view object
 *
 */
@Data
public class ClientVO {

    private Long id;
    private String respType;
    private String description;
    private String clientId;
    private String clientSecret;
    private String basicAuth;
    private String redirectUrl;
    private String ownerName;
    private Set<String> scopes;
    private String rememberChoice;

}
