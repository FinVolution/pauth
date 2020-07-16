package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * authentication holder entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "authentication_holder", schema = "pauth", catalog = "")
public class AuthenticationHolderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "user_id")
    private Long userId;

    @Basic
    @Column(name = "approved")
    private Boolean approved;

    @Basic
    @Column(name = "redirect_uri")
    private String redirectUri;

    @Basic
    @Column(name = "client_id")
    private String clientId;

    public void setAuthentication(OAuth2Authentication o2Authentication) {
        OAuth2Request o2Request = o2Authentication.getOAuth2Request();
        this.setClientId(o2Request.getClientId());
        this.setApproved(o2Request.isApproved());
        this.setRedirectUri(o2Request.getRedirectUri());
    }

}
