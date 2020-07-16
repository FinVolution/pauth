package com.ppdai.auth.service;

import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.po.UserEntity;
import com.ppdai.auth.vo.ClientVO;
import com.ppdai.auth.vo.PageVO;

import java.util.List;

/**
 * client service
 *
 */
public interface ClientService {

    void register(ClientVO clientVO);

    List<ClientEntity> fetchAllClients();

    PageVO<ClientEntity> fetchClientsByPage(String clientId, Long ownerId, int page, int size);

    List<ClientEntity> fetchClientByUser(UserEntity owner);

    void removeById(Long clientId);

    void updateById(ClientEntity client);

    String getName(Long clientId);

    ClientEntity findByClientName(String clientName);

}
