package com.ppdai.auth.common.identity;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of identity
 *
 */
public class DefaultIdentity implements Identity {

    private String userName;
    private String userEmail;
    private String userOrg;
    private String userRole;
    private Map<String, String> additonalInformation;

    public DefaultIdentity(String username) {
        this.userName = username;
        additonalInformation = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.userName;
    }

    @Override
    public void setName(String name) {
        this.userName = name;
    }

    @Override
    public String getEmail() {
        return this.userEmail;
    }

    @Override
    public void setEmail(String email) {
        this.userEmail = email;
    }

    @Override
    public String getOrganzation() {
        return this.userOrg;
    }

    @Override
    public void setOrganzation(String organzation) {
        this.userOrg = organzation;
    }

    @Override
    public String getRole() {
        return this.userRole;
    }

    @Override
    public void setRole(String role) {
        this.userRole = role;
    }

    @Override
    public Map<String, String> getAdditionalInfomation() {
        return this.additonalInformation;
    }

    @Override
    public void fillAdditionalInfomation(String key, String value) {
        this.additonalInformation.put(key, value);
    }


}
