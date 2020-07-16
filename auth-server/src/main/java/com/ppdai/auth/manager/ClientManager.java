package com.ppdai.auth.manager;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.service.ClientService;
import com.ppdai.auth.vo.ClientVO;
import com.ppdai.auth.vo.ValidityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Client manager
 *
 */
@Service
public class ClientManager {

    @Autowired
    private ClientService clientService;

    /**
     * 对Client实体信息做合法性校验，包括检查ClientId, Scopes，RedirectUrl
     *
     * @param clientVO Client实体信息
     * @return 返回Client合法性检查结果
     */
    public ValidityVO introspectClient(ClientVO clientVO) {
        // 合法性检查结果，初始为False
        ValidityVO validityVO = new ValidityVO();

        // verify client id
        String clientId = clientVO.getClientId();
        ClientEntity client = clientService.findByClientName(clientId);
        if (client != null) {

            // TODO: verify client scopes, Note: currently there are fixed scopes by definition
            Set<String> scopes = clientVO.getScopes();

            // verify client redirect url
            String redirectUrl = clientVO.getRedirectUrl();

            // redirectUrl的正则表达式校验
            boolean isMatch = false;
            try {
                isMatch = Pattern.matches(client.getRedirectUrl(), redirectUrl);
            } catch (PatternSyntaxException e) {
                throw new BaseException(MessageType.ERROR, "应用回调地址校验异常，请确保注册的地址格式正确。");
            }

            validityVO.setIsValid(isMatch);
        }

        return validityVO;
    }

}
