package com.ppdai.auth.scheduler;

import com.ppdai.auth.dao.AccessTokenRepository;
import com.ppdai.auth.dao.AuditLogRepository;
import com.ppdai.auth.dao.AuthorizationCodeRepository;
import com.ppdai.auth.dao.RefreshTokenRepository;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.po.AccessTokenEntity;
import com.ppdai.auth.po.AuditLogEntity;
import com.ppdai.auth.po.RefreshTokenEntity;
import com.ppdai.auth.utils.EnvProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * the utils of clean task
 *
 */
@Component
public class OAuth2TokenExpireUtil {

    @Autowired
    private AuthorizationCodeRepository authCodeRepo;
    @Autowired
    private AccessTokenRepository accessTokenRepo;
    @Autowired
    private RefreshTokenRepository refreshTokenRepo;
    @Autowired
    private AuditLogRepository auditLogRepo;

    @Autowired
    private EnvProperty envProperty;

    @Transactional(rollbackFor = BaseException.class)
    public void removeAuthCode() {
        authCodeRepo.removeByTimeBefore(this.getNow());
    }

    @Transactional(rollbackFor = BaseException.class)
    public void removeAccessToken() {
        accessTokenRepo.removeByTimeBefore(this.getNow());
    }

    @Transactional(rollbackFor = BaseException.class)
    public void removeRefreshToken() {
        refreshTokenRepo.removeByTimeBefore(this.getNow());
    }

    @Transactional(rollbackFor = BaseException.class)
    public void cleanAuthCode() {
        authCodeRepo.deleteByTimeBefore(this.getNow());
    }

    @Transactional(rollbackFor = BaseException.class)
    public void cleanAccessToken() {
        accessTokenRepo.deleteByTimeBefore(this.getNow());
    }

    @Transactional(rollbackFor = BaseException.class)
    public void cleanRefreshToken() {
        /**
         * 注：不能使用refreshTokenRepo.deleteByTimeBefore(this.getNow())直接删除所有的过期refresh token
         * 原因是有些refresh token过期了，但是access token还未过期失效
         * 正确清理方法：找到所有过期的refresh token，检查是否有未过期的access token，若没有，则删除该refresh token
         */
        Iterable<RefreshTokenEntity> refreshTokens = refreshTokenRepo.findInactiveTokenByTimeBefore(this.getNow());
        for (RefreshTokenEntity refreshToken : refreshTokens) {
            Iterable<AccessTokenEntity> accessTokens = accessTokenRepo.findByRefreshToken(refreshToken);
            // 若所有相关的access token已删除，则可以清理该refresh token
            if (!accessTokens.iterator().hasNext()) {
                refreshTokenRepo.delete(refreshToken);
            }
        }
    }

    /**
     * 清理15天前的审计日志
     */
    @Transactional(rollbackFor = BaseException.class)
    public void cleanAuditLog() {
        Timestamp expired = this.getTime(-15 * 24 * 60 * 60 * 1000L);
        auditLogRepo.deleteByTimeBefore(expired);
    }

    public Timestamp getOAuthCodeExpireTime() {
        return new Timestamp(System.currentTimeMillis() + envProperty.OAUTH_EXPIRE_CODE);
    }

    public Timestamp getAccessTokenExpireTime() {
        return new Timestamp(System.currentTimeMillis() + envProperty.OAUTH_EXPIRE_ACCESS);
    }

    public Timestamp getRefreshTokenExpireTime() {
        return new Timestamp(System.currentTimeMillis() + envProperty.OAUTH_EXPIRE_REFRESH);
    }

    public Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getTime(Long diff) {
        return new Timestamp(System.currentTimeMillis() + diff);
    }

}
