package com.ppdai.auth.config;

import java.util.ArrayList;
import java.util.List;

import com.ppdai.auth.utils.EnvProperty;
import com.ppdai.auth.utils.LdapProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LDAPConfiguration {

    /**
     * envProperty is used to fetch dynamic settings from apollo, the environment configuration center of ppdai
     */
    @Autowired
    private EnvProperty envProperty;

    /**
     * ldapProperty is implemented by ConfigurationProperties, which is initialized at app startup
     */
    @Autowired
    private LdapProperty ldapProperty;

    public String getServer() {
        return envProperty.getProperty("app.ldap.server");
    }

    public List<String> getPaths() {
        List<String> paths = new ArrayList<>();

        if (ldapProperty.getPaths() != null) {
            paths = ldapProperty.getPaths();
        }

        return paths;
    }
}
