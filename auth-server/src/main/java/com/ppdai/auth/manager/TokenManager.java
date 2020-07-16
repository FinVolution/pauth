package com.ppdai.auth.manager;

import com.ppdai.auth.common.identity.Identity;
import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.utils.JwtUtil;
import com.ppdai.auth.dao.*;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.exception.UnAuthorizeException;
import com.ppdai.auth.idp.AuthenticationProvider;
import com.ppdai.auth.po.*;
import com.ppdai.auth.service.AuthHolderService;
import com.ppdai.auth.service.ClientService;
import com.ppdai.auth.service.MapService;
import com.ppdai.auth.service.OAuth2Service;
import com.ppdai.auth.utils.ConvertUtil;
import com.ppdai.auth.vo.AuthCodeVO;
import com.ppdai.auth.vo.AuthTokenVO;
import com.ppdai.auth.vo.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Access/Refresh Token的管理
 *
 */
@Service
public class TokenManager {

    @Autowired
    private OAuth2Service oAuth2Service;
    @Autowired
    private AuthorizationCodeRepository authCodeRepo;
    @Autowired
    private AccessTokenRepository accessTokenRepo;
    @Autowired
    private RefreshTokenRepository refreshTokenRepo;
    @Autowired
    private AuthenticationHolderRepository authenticationHolderRepo;

    @Autowired
    private MapService mapperService;

    @Autowired
    @Qualifier(value = "localIdpProvider")
    private AuthenticationProvider localIdpProvider;

    @Autowired
    @Qualifier(value = "atlasLdpProvider")
    private AuthenticationProvider atlasLdpProvider;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthenticationHolderRepository authHolderRepo;

    @Autowired
    private ClientRepository clientRepo;

    public List<AuthCodeVO> getAllAuthCodes() {
        Iterable<AuthorizationCodeEntity> authCodes = authCodeRepo.findAllEx();
        return ConvertUtil.convert(authCodes, mapperService::mapAuthCode);
    }

    public PageVO<AuthCodeVO> getAuthCodesByPage(Long userId, String clientId, Integer page, Integer size) {
        PageVO<AuthCodeVO> codePageVO = new PageVO<>();
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(page, size, sort);

        Page<AuthorizationCodeEntity> codePage = authCodeRepo.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (userId != null) {
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), userId));
            }
            if (!"".equals(clientId)) {
                list.add(criteriaBuilder.equal(root.get("clientId").as(String.class), clientId));
            }
            list.add(criteriaBuilder.equal(root.get("isActive").as(Boolean.class), Boolean.TRUE));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        List<AuthorizationCodeEntity> codeList = codePage.getContent();
        List<AuthCodeVO> codeVOList = ConvertUtil.convert(codeList, mapperService::mapAuthCode);
        codePageVO.setContent(codeVOList);
        codePageVO.setTotalElements(codePage.getTotalElements());
        return codePageVO;
    }

    public List<AuthTokenVO> getAllAccessTokens() {
        Iterable<AccessTokenEntity> tokens = accessTokenRepo.findAllEx();
        List<AuthTokenVO> tokenVOs = ConvertUtil.convert(tokens, mapperService::mapAccessToken);
        return tokenVOs;
    }

    public PageVO<AuthTokenVO> getAccessTokensByPage(Long userId, String clientId, Integer page, Integer size) {
        PageVO<AuthTokenVO> tokenPageVO = new PageVO<>();
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(page, size, sort);

        Page<AccessTokenEntity> tokenPage = accessTokenRepo.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (userId != null) {
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), userId));
            }
            if (!clientId.isEmpty()) {
                list.add(criteriaBuilder.equal(root.get("clientId").as(String.class), clientId));
            }
            list.add(criteriaBuilder.equal(root.get("isActive").as(Boolean.class), Boolean.TRUE));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        List<AccessTokenEntity> tokenList = tokenPage.getContent();
        List<AuthTokenVO> tokenVOList = ConvertUtil.convert(tokenList, mapperService::mapAccessToken);
        tokenPageVO.setContent(tokenVOList);
        tokenPageVO.setTotalElements(tokenPage.getTotalElements());
        return tokenPageVO;
    }

    public List<AuthTokenVO> getAllRefreshTokens() {
        Iterable<RefreshTokenEntity> tokens = refreshTokenRepo.findAllEx();
        List<AuthTokenVO> tokenVOs = ConvertUtil.convert(tokens, mapperService::mapRefreshToken);
        return tokenVOs;
    }

    public PageVO<AuthTokenVO> getRefreshTokensByPage(Long userId, String clientId, Integer page, Integer size) {
        PageVO<AuthTokenVO> tokenPageVO = new PageVO<>();
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(page, size, sort);

        Page<RefreshTokenEntity> tokenPage = refreshTokenRepo.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (userId != null) {
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), userId));
            }
            if (!"".equals(clientId)) {
                list.add(criteriaBuilder.equal(root.get("clientId").as(String.class), clientId));
            }
            list.add(criteriaBuilder.equal(root.get("isActive").as(Boolean.class), Boolean.TRUE));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        List<RefreshTokenEntity> tokenList = tokenPage.getContent();
        List<AuthTokenVO> tokenVOList = ConvertUtil.convert(tokenList, mapperService::mapRefreshToken);
        tokenPageVO.setContent(tokenVOList);
        tokenPageVO.setTotalElements(tokenPage.getTotalElements());
        return tokenPageVO;
    }

    @Transactional(rollbackFor = BaseException.class)
    public Boolean revokeAuthCode(Long codeId) {
        authCodeRepo.removeById(codeId);
        return Boolean.TRUE;
    }

    public Boolean revokeAccessToken(Long tokenId) {
        AccessTokenEntity token = accessTokenRepo.findOne(tokenId);
        return oAuth2Service.revokeToken(token.getValue());
    }

    public Boolean revokeRefreshToken(Long tokenId) {
        RefreshTokenEntity token = refreshTokenRepo.findOne(tokenId);
        return oAuth2Service.revokeToken(token.getValue());
    }

    public void extendTokenExpiration(Long tokenId, Long overtime) {
        AccessTokenEntity token = accessTokenRepo.findByIdEx(tokenId);
        if (token == null) {
            throw BaseException.newException(MessageType.ERROR, "token不存在或已过期。");
        }
        Timestamp expireTime = new Timestamp(token.getExpiration().getTime() + overtime);
        token.setExpiration(expireTime);
        accessTokenRepo.save(token);
    }

    public OAuth2AccessToken createLongtimeToken(String userName, String clientId, Long lifeCycle) {
        ClientEntity client = clientRepo.findByIdEx(clientId);
        if (client == null) {
            throw new UnAuthorizeException(MessageType.ERROR, "Client不存在。");
        }

        // 先从远程获取获取用户信息，若没有找到，则尝试从本地数据库获取
        Identity identity = atlasLdpProvider.load(userName);
        if (identity == null) {
            identity = localIdpProvider.load(userName);
        }

        if(identity == null){
            throw UnAuthorizeException.newException(MessageType.ERROR, "该用户不存在。");
        }

        //获取登录用户的角色，判断登录用户是否有权限为指定userName生成长期token
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String jwtToken = request.getHeader("jwt-token");
        Identity identityLogIn = JwtUtil.decode(jwtToken);

        //只有在atlas上配置了docker角色的用户，才可以生成长期有效的token
        if(identityLogIn.getRole() != null && identityLogIn.getRole().contains("admin")){
            UserEntity user = userRepo.findOneByName(userName);
            Long userId;
            if(user != null) {
                userId = user.getId();
            }else {
                //PAuth管理员为指定用户生成长期token时，如果此用户尚未登录过PAuth，就直接在User表中插入一条。（正常user表的数据是在用户登录PAuth的时候才会插入。）
                user = new UserEntity();
                user.setName(userName);
                user.setEmail(identity.getEmail());
                user.setLastVisitAt(new Date());
                userRepo.save(user);
                userId = user.getId();
            }

            OAuth2Request oAuth2Request = new OAuth2Request(null, clientId, null, true, null, null, null, null, null);
            UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken(userName, null);
            OAuth2Authentication o2Authentication =  new OAuth2Authentication(oAuth2Request, userAuthentication);
            AuthenticationHolderEntity authHolder = new AuthenticationHolderEntity();
            authHolder.setAuthentication(o2Authentication);
            authHolder.setUserId(userId);
            authHolderRepo.save(authHolder);

            Timestamp expireTime = new Timestamp(System.currentTimeMillis() + lifeCycle);
            String accessToken = JwtUtil.encode(identity, expireTime, "secret", "pauth");
            AccessTokenEntity o2AccessToken = new AccessTokenEntity();
            o2AccessToken.setAuthHolderId(authHolder.getId());
            o2AccessToken.setTokenValue(accessToken);
            o2AccessToken.setExpiration(expireTime);
            o2AccessToken.setUserId(userId);
            o2AccessToken.setClientId(clientId);
            o2AccessToken = accessTokenRepo.save(o2AccessToken);

            return o2AccessToken;
        }else{
            throw UnAuthorizeException.newException(MessageType.ERROR, "没有生成长期token的权限，请联系pauth系统管理员。");
        }
    }
}
