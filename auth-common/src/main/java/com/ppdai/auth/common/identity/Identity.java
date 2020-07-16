package com.ppdai.auth.common.identity;

import java.util.Map;

/**
 * Identity Information
 *
 */
public interface Identity {

    /**
     * read identity name
     *
     * @return identity name
     */
    String getName();

    /**
     * write identity name
     *
     * @param name identity name
     */
    void setName(String name);

    /**
     * read identity email address
     *
     * @return identity email address
     */
    String getEmail();

    /**
     * write identity email address
     *
     * @param email identity email address
     */
    void setEmail(String email);

    /**
     * read identity organization name
     *
     * @return identity organization name
     */
    String getOrganzation();

    /**
     * write identity organization name
     *
     * @param organzation identity organization name
     */
    void setOrganzation(String organzation);

    /**
     * read identity roles
     *
     * @return identity roles
     */
    String getRole();

    /**
     * write identity roles
     *
     * @param role identity roles
     */
    void setRole(String role);

    /**
     * identity additional information
     *
     * @return identity additional information
     */
    Map<String, String> getAdditionalInfomation();

    /**
     * a method to fill identity additional information
     *
     * @param key   the key of additional information
     * @param value the value of additional information
     */
    void fillAdditionalInfomation(String key, String value);

}
