package com.ppdai.auth.manager;

import com.ppdai.auth.dao.AccessTokenRepository;
import com.ppdai.auth.dao.AuthorizationCodeRepository;
import com.ppdai.auth.dao.RefreshTokenRepository;
import com.ppdai.auth.dao.UserRepository;
import com.ppdai.auth.po.*;
import com.ppdai.auth.service.*;
import com.ppdai.auth.utils.ConvertUtil;
import com.ppdai.auth.vo.AuthCodeVO;
import com.ppdai.auth.vo.AuthTokenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取用户所拥有的信息，包括：授权码、Access/Refresh Token、Clients、授权通过的登录sessions
 *
 */
@Service
public class UserOwnedRrcManager {

    @Autowired
    private AuthorizationCodeRepository authCodeRepo;
    @Autowired
    private AccessTokenRepository accessTokenRepo;
    @Autowired
    private RefreshTokenRepository refreshTokenRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ClientService clientService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthHolderService authHolderService;
    @Autowired
    private ApprovedSiteService approvedSiteService;

    @Autowired
    private MapService mapperService;

    public List<ApprovedSiteEntity> getMyApprovedSites(String username) {
        return (List<ApprovedSiteEntity>) approvedSiteService.getSitesByUserName(username);
    }

    public List<ClientEntity> getMyClients(String username) {
        UserEntity owner = userService.findUserByName(username);
        return clientService.fetchClientByUser(owner);
    }

    public List<AuthCodeVO> getMyAuthCodes(String username) {
        UserEntity user = userRepo.findOneByName(username);
        Iterable<AuthorizationCodeEntity> authCodeList = authCodeRepo.findByUserId(user.getId());
        List<AuthCodeVO> authCodeVOList = ConvertUtil.convert(authCodeList, mapperService::mapAuthCode);
        return authCodeVOList;
    }

    public List<AuthTokenVO> getMyAccessTokens(String username) {
        UserEntity user = userRepo.findOneByName(username);
        Iterable<AccessTokenEntity> accessTokenList = accessTokenRepo.findByUserId(user.getId());
        List<AuthTokenVO> authTokenVOList = ConvertUtil.convert(accessTokenList, mapperService::mapAccessToken);
        return authTokenVOList;
    }

    public List<AuthTokenVO> getMyRefreshTokens(String username) {
        UserEntity user = userRepo.findOneByName(username);
        Iterable<RefreshTokenEntity> refreshTokenList = refreshTokenRepo.findByUserId(user.getId());
        List<AuthTokenVO> authTokenVOList = ConvertUtil.convert(refreshTokenList, mapperService::mapRefreshToken);
        return authTokenVOList;
    }

}
