package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * client entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "client", schema = "pauth", catalog = "")
public class ClientEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "client_id")
    private String clientId;

    @Basic
    @Column(name = "client_secret")
    private String clientSecret;

    @Basic
    @Column(name = "basic_auth")
    private String basicAuth;

    @Basic
    @Column(name = "redirect_url")
    private String redirectUrl;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Basic
    @Column(name = "reuse_refresh_tokens", nullable = false)
    private Boolean reuseRefreshTokens = false;
}
