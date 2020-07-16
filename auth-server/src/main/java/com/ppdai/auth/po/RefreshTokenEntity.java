package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * refresh token entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "refresh_token", schema = "pauth", catalog = "")
public class RefreshTokenEntity extends BaseEntity implements OAuth2RefreshToken {

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
    @Column(name = "auth_holder_id")
    private Long authHolderId;

    @Basic
    @Column(name = "user_id")
    private Long userId;

    @Basic
    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Override
    public String getValue() {
        return this.tokenValue;
    }
}
