package com.ppdai.auth.service;

import com.ppdai.auth.po.ApprovedSiteEntity;
import com.ppdai.auth.vo.PageVO;

import java.util.Set;

/**
 * approved site service
 *
 */
public interface ApprovedSiteService {

    /**
     * fetch the list of sites approved by user
     *
     * @param username user name
     * @return the list of approved sites, returns empty list if there is no approved site
     */
    Iterable<ApprovedSiteEntity> getSitesByUserName(String username);

    /**
     * get the page list of approved sites by user
     *
     * @param username user name
     * @param clientId client id
     * @param page     page index
     * @param size     page size
     * @return the list of approved sites, returns empty list if there is no approved site
     */
    PageVO<ApprovedSiteEntity> getSitesByPage(String username, String clientId, Integer page, Integer size);

    /**
     * save a approved site by user
     *
     * @param userName user name
     * @param clientId client id
     * @param scopes   approved scopes
     * @return approved site by saved
     */
    ApprovedSiteEntity record(String userName, String clientId, Set<String> scopes);

    /**
     * remove a site by id
     *
     * @param siteId site id
     */
    void removeSiteById(Long siteId);

    /**
     * check if there is any approval for client's site by user
     *
     * @param username user name
     * @param clientId client id
     * @return true if there is existing approval
     */
    Boolean existApprovalFor(String username, String clientId);

}
