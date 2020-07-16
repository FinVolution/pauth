package com.ppdai.auth.service.impl;

import com.ppdai.auth.common.utils.BasicAuthUtil;
import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.dao.ClientRepository;
import com.ppdai.auth.dao.UserRepository;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.po.UserEntity;
import com.ppdai.auth.service.ClientService;
import com.ppdai.auth.service.UserService;
import com.ppdai.auth.utils.ConvertUtil;
import com.ppdai.auth.vo.ClientVO;
import com.ppdai.auth.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Please add description here.
 *
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepo;


    private RandomValueStringGenerator generator = new RandomValueStringGenerator();

    @Transactional(rollbackFor = BaseException.class)
    @Override
    public void register(ClientVO clientVO) {

        String clientId = clientVO.getClientId().trim();
        if (clientId == null || clientId.isEmpty()) {
            throw BaseException.newException(MessageType.ERROR, "对不起，Client名为空，无法注册Client。请输入Client名。");
        }

        // 检查redirectUrl的合法性
//        Boolean isRedirectUrlValid = clientVO.getRedirectUrl().startsWith("http://") || clientVO.getRedirectUrl().startsWith("https://");
//        if (!isRedirectUrlValid) {
//            throw BaseException.newException(MessageType.ERROR, "重定向返回地址非法，请以http://或https://开头。");
//        }

        String redirectUrl = clientVO.getRedirectUrl().trim();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            throw BaseException.newException(MessageType.ERROR, "对不起，重定向返回地址不能为空，请重新输入。");
        }

        // 检查client是否存在，若存在但已经被删除则更新client信息,否则创建新的client
        ClientEntity existingClient = clientRepo.findByClientId(clientId);
        if (existingClient != null) {
            if (existingClient.getIsActive() == true) {
                throw BaseException.newException(MessageType.ERROR, "对不起，%s已被注册过，请选择另外一个Client名。", clientId);
            } else {
                // 为client生成新的secret和basicAuth
                String clientSecret = generator.generate();
                existingClient.setClientSecret(clientSecret);
                String basicAuth = BasicAuthUtil.encode(clientId, clientSecret);
                existingClient.setBasicAuth(basicAuth);

                UserEntity user = userService.findUserByName(clientVO.getOwnerName());
                existingClient.setOwner(user);
                
                existingClient.setDescription(clientVO.getDescription());
                existingClient.setRedirectUrl(redirectUrl);
                existingClient.setIsActive(true);

                clientRepo.save(existingClient);
            }
        } else {
            clientVO.setClientId(clientId);
            clientVO.setRedirectUrl(redirectUrl);

            // 为新client生成secret和basicAuth
            String clientSecret = generator.generate();
            clientVO.setClientSecret(clientSecret);
            String basicAuth = BasicAuthUtil.encode(clientId, clientSecret);
            clientVO.setBasicAuth(basicAuth);

            ClientEntity client = ConvertUtil.convert(clientVO, ClientEntity.class);

            UserEntity user = userService.findUserByName(clientVO.getOwnerName());
            client.setOwner(user);

            clientRepo.save(client);
        }
    }

    @Override
    public List<ClientEntity> fetchAllClients() {
        return clientRepo.findAllEx();
    }

    @Override
    public PageVO<ClientEntity> fetchClientsByPage(String clientId, Long ownerId, int page, int size) {
        PageVO<ClientEntity> pageVO = new PageVO<>();
        Pageable pageable = new PageRequest(page, size);

        Page<ClientEntity> clientPage = clientRepo.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(criteriaBuilder.like(root.get("clientId").as(String.class), "%" + clientId + "%"));
            if (ownerId != null) {
                UserEntity owner = userRepo.findOne(ownerId);
                list.add(criteriaBuilder.equal(root.get("owner").as(UserEntity.class), owner));
            }
            list.add(criteriaBuilder.equal(root.get("isActive").as(Boolean.class), Boolean.TRUE));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        pageVO.setContent(clientPage.getContent());
        pageVO.setTotalElements(clientPage.getTotalElements());
        return pageVO;
    }

    @Override
    public List<ClientEntity> fetchClientByUser(UserEntity owner) {
        return clientRepo.findByOwner(owner);
    }

    @Transactional(rollbackFor = BaseException.class)
    @Override
    public void removeById(Long clientId) {
        clientRepo.removeById(clientId);
    }

    @Transactional(rollbackFor = BaseException.class)
    @Override
    public void updateById(ClientEntity clientEntity) {
        //检查redirectUrl的合法性
//        Boolean isRedirectUrlValid = clientEntity.getRedirectUrl().startsWith("http://") || clientEntity.getRedirectUrl().startsWith("https://");
//        if (!isRedirectUrlValid) {
//            throw BaseException.newException(MessageType.ERROR, "重定向返回地址非法，请以http://或https://开头。");
//        }

        String redirectUrl = clientEntity.getRedirectUrl().trim();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            throw BaseException.newException(MessageType.ERROR, "对不起，重定向返回地址不能为空，请重新输入。");
        }

        ClientEntity client = clientRepo.findOne(clientEntity.getId());
        client.setRedirectUrl(redirectUrl);
        client.setDescription(clientEntity.getDescription());
        clientRepo.save(client);
    }

    @Override
    public String getName(Long id) {
        ClientEntity client = clientRepo.findOne(id);
        return client.getClientId();
    }

    @Override
    public ClientEntity findByClientName(String clientName) {
        return clientRepo.findByIdEx(clientName);
    }

}
