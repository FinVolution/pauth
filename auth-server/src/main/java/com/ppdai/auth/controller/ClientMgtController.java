package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.auth.manager.ClientManager;
import com.ppdai.auth.manager.UserOwnedRrcManager;
import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.service.ApprovedSiteService;
import com.ppdai.auth.service.ClientService;
import com.ppdai.auth.utils.RequestContextUtil;
import com.ppdai.auth.vo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用开发者的Client注册接口，申请Client Id和Secret
 *
 */
@RestController
@RequestMapping("/api/clients")
public class ClientMgtController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private UserOwnedRrcManager userOwnedRrcManager;
    @Autowired
    private ClientManager clientManager;
    @Autowired
    private ApprovedSiteService approvedSiteService;

    @ApiOperation(value = "获取client列表")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Response<List<ClientEntity>> getClientList(@RequestParam(value = "username", required = false) String username) {
        List<ClientEntity> clients;
        if (username == null || username.isEmpty()) {
            clients = clientService.fetchAllClients();
        } else {
            clients = userOwnedRrcManager.getMyClients(username);
        }
        return Response.mark(MessageType.SUCCESS, clients);
    }

    @ApiOperation(value = "获取分页client列表")
    @RequestMapping(method = RequestMethod.GET)
    public Response<PageVO<ClientEntity>> getClientsByPage(@RequestParam String clientId,
                                                           @RequestParam Long ownerId,
                                                           @RequestParam Integer page,
                                                           @RequestParam Integer size) {
        PageVO<ClientEntity> clientPageVO = clientService.fetchClientsByPage(clientId, ownerId, page, size);
        return Response.mark(MessageType.SUCCESS, clientPageVO);
    }

    @ApiOperation(value = "创建client")
    @RequestMapping(method = RequestMethod.POST)
    public Response<String> createNewClient(@RequestBody ClientVO clientVO) {
        clientService.register(clientVO);
        return Response.mark(MessageType.SUCCESS, "应用[%s]注册成功。", clientVO.getClientId().trim());
    }

    @ApiOperation(value = "更新client")
    @RequestMapping(value = "/{clientId}", method = RequestMethod.PUT)
    public Response<String> updateClientById(@PathVariable Long clientId, @RequestBody ClientEntity client) {
        clientService.updateById(client);
        return Response.mark(MessageType.SUCCESS, "应用[id=%s]信息更新成功。", clientId);
    }

    @ApiOperation(value = "删除client")
    @RequestMapping(value = "/{clientId}", method = RequestMethod.DELETE)
    public Response<String> removeClientById(@PathVariable Long clientId) {
        clientService.removeById(clientId);
        return Response.mark(MessageType.SUCCESS, "应用[id=%s]已经删除成功。", clientId);
    }

    @ApiOperation(value = "校验client的合法性")
    @RequestMapping(value = "/introspect", method = RequestMethod.POST)
    public Response<ClientCheckResultVO> verifyClient(@RequestBody IntrospectVO introspectVO) {
        String username = introspectVO.getUsername();
        ClientVO clientVO = introspectVO.getClientVO();

        ClientCheckResultVO checkResult = new ClientCheckResultVO();

        ValidityVO validityVO = clientManager.introspectClient(clientVO);
        checkResult.setIsValid(validityVO.getIsValid());

        //在client校验为合法时，检查是否可以直接进行授权
        if (validityVO.getIsValid()) {
            Boolean existApproval = approvedSiteService.existApprovalFor(username, clientVO.getClientId());
            checkResult.setDirectApprove(existApproval);
        }

        return Response.mark(MessageType.SUCCESS, checkResult);
    }

}
