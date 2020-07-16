package com.ppdai.auth.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A scheduler of oauth2 server clean task
 *
 */
@EnableScheduling
@ConditionalOnProperty(name = "app.scheduler.enable", havingValue = "true")
@Component
@Slf4j
public class OAuth2TokenCleanTask {

    @Autowired
    private OAuth2TokenExpireUtil oAuthExpireUtil;

    @Scheduled(cron = "${app.scheduler.token.removal}")
    public void cleanExpiredToken() {
        log.info("开始删除过期的授权码和令牌（数据库中设置删除标记为true）...");
        try {
            oAuthExpireUtil.removeAccessToken();
            oAuthExpireUtil.removeAuthCode();
            oAuthExpireUtil.removeRefreshToken();
        } catch (Exception e) {
            log.error("在删除过期的授权码和令牌过程中出现异常。", e.getMessage());
        }
        log.info("删除完成。");
    }

    @Scheduled(cron = "${app.scheduler.token.clean}")
    public void removeExpiredToken() {
        log.info("开始清理过期的授权码和令牌（从数据库永久删除数据）...");
        try {
            oAuthExpireUtil.cleanAccessToken();
            oAuthExpireUtil.cleanAuthCode();
            oAuthExpireUtil.cleanRefreshToken();
            // 清理过期的审计日志
//            oAuthExpireUtil.cleanAuditLog();
        } catch (Exception e) {
            log.error("在清理过期的授权码和令牌过程中出现异常。", e.getMessage());
        }
        log.info("清理完成。");
    }

}
