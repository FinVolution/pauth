package com.ppdai.auth.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Use ConfigurationProperties to read a array of ldap search paths.
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.ldap.search")
public class LdapProperty {
    private List<String> paths;
}
