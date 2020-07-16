package com.ppdai.auth.service;

import org.springframework.stereotype.Service;

/**
 * auth holder service
 *
 */
@Service
public interface AuthHolderService {

    /**
     * get user name by an auth holder
     *
     * @param holderId auth holder id
     * @return user name
     */
    String getUserNameByHolderId(Long holderId);

    /**
     * get client name by an auth holder
     *
     * @param holderId auth holder id
     * @return client name
     */
    String getClientNameByHolderId(Long holderId);

}
