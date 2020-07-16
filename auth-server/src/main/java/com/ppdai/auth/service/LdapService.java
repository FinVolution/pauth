package com.ppdai.auth.service;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.ppdai.auth.config.LDAPConfiguration;
import com.ppdai.auth.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Please add description here.
 *
 */
@Slf4j
@Service
public class LdapService {
    @Autowired
    private LDAPConfiguration ldapConfig;

    private UserVO getUser(String username, String password, String searchPath) {
        UserVO user = null;
        try {
            Properties env = new Properties();

            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "corp\\" + username);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(Context.PROVIDER_URL, ldapConfig.getServer());

            LdapContext ctx = new InitialLdapContext(env, null);
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String searchFilter = String.format("(&(objectCategory=person)(objectClass=user)(SAMAccountName=%s))",
                    username);

            String[] returnedAtts = {"memberOf", "sAMAccountName", "cn", "distinguishedName", "mail"};
            searchCtls.setReturningAttributes(returnedAtts);
            NamingEnumeration<SearchResult> answer = ctx.search(searchPath, searchFilter, searchCtls);
            if (answer.hasMoreElements()) {

                SearchResult sr = (SearchResult) answer.next();
                Attributes attribute = sr.getAttributes();
                user = new UserVO();
                user.setName(username);
                user.setEmail(getValue(attribute.get("mail")));
                ctx.close();
                return user;
            }
        } catch (NamingException e) {
            log.error(String.format("LDAP validate user %s error: %s", username, e.getMessage()), e);

        }
        return user;
    }

    private String getValue(Attribute attribute) {
        if (attribute == null) {
            return "";
        }
        String value = attribute.toString();
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (value.indexOf(":") != -1) {
            value = value.replaceAll(value.split(":")[0], "").trim();
            value = value.substring(1).trim();
        }
        return value;
    }

    public UserVO login(String username, String password) {
        for (String searchPath : ldapConfig.getPaths()) {
            UserVO user = getUser(username, password, searchPath);
            if (user != null) {
                return user;
            }
        }
        return null;
    }
}
