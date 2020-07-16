package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.common.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * access token entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "access_token", schema = "pauth", catalog = "")
public class AccessTokenEntity extends BaseEntity implements OAuth2AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "token_value")
    private String tokenValue;

    @Basic
    @Column(name = "expiration")
    private Timestamp expiration;

    @Basic
    @Column(name = "token_type")
    private String tokenType = OAuth2AccessToken.BEARER_TYPE;

    @Basic
    @Column(name = "auth_holder_id")
    private Long authHolderId;

    @Basic
    @Column(name = "user_id")
    private Long userId;

    @Basic
    @Column(name = "client_id", nullable = false)
    private String clientId;

    @ManyToOne(targetEntity = RefreshTokenEntity.class)
    @JoinColumn(name = "refresh_token_id")
    private RefreshTokenEntity refreshToken;

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return new HashMap<>(16);
    }

    @Override
    public Set<String> getScope() {
        return new HashSet<>();
    }

    @Override
    public OAuth2RefreshToken getRefreshToken() {
        return this.refreshToken;
    }

    @Override
    public boolean isExpired() {
        return getExpiration() != null && System.currentTimeMillis() > getExpiration().getTime();
    }

    @Override
    public int getExpiresIn() {
        if (getExpiration() == null) {
            // no expiration time
            return -1;
        } else {
            int secondsRemaining = (int) ((getExpiration().getTime() - System.currentTimeMillis()) / 1000);
            if (isExpired()) {
                // has an expiration time and expired
                return 0;
            } else {
                // has an expiration time and not expired
                return secondsRemaining;
            }
        }
    }

    @Override
    public String getValue() {
        return this.tokenValue;
    }
}
