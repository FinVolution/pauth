package com.ppdai.auth.service.impl;

import com.ppdai.auth.dao.AuthenticationHolderRepository;
import com.ppdai.auth.dao.ClientRepository;
import com.ppdai.auth.dao.UserRepository;
import com.ppdai.auth.po.AuthenticationHolderEntity;
import com.ppdai.auth.po.UserEntity;
import com.ppdai.auth.service.AuthHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Please add description here.
 *
 */
@Service
public class AuthHolderServiceImpl implements AuthHolderService {

    @Autowired
    private AuthenticationHolderRepository authHolderRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ClientRepository clientRepo;

    @Override
    public String getUserNameByHolderId(Long holderId) {
        String userName = null;

        if (holderId != null) {
            AuthenticationHolderEntity authHolder = authHolderRepo.findOne(holderId);
            if (authHolder != null) {
                Long userId = authHolder.getUserId();
                if (userId != null) {
                    UserEntity user = userRepo.findOne(userId);
                    userName = user.getName();
                    if (userName == null) {
                        userName = user.getEmail();
                    }
                }
            }
        }

        return userName;
    }

    @Override
    public String getClientNameByHolderId(Long holderId) {
        String clientName = null;

        if (holderId != null) {
            AuthenticationHolderEntity authHolder = authHolderRepo.findOne(holderId);
            if (authHolder != null) {
                clientName = authHolder.getClientId();
            }
        }

        return clientName;
    }

}
