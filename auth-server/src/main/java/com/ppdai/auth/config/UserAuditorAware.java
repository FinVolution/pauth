package com.ppdai.auth.config;

import com.ppdai.auth.utils.RequestContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@Slf4j
public class UserAuditorAware implements AuditorAware<String> {

    public static final String DEFAULT_SYSTEM_NAME = "system";

    @Override
    public String getCurrentAuditor() {
        String userName = RequestContextUtil.getCurrentUserName();

        if (userName == null) {
            userName = DEFAULT_SYSTEM_NAME;
        }

        return userName;
    }

}
