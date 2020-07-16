package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * authorization code entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "authorization_code", schema = "pauth", catalog = "")
public class AuthorizationCodeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "code")
    private String code;

    @Basic
    @Column(name = "auth_holder_id")
    private Long authHolderId;

    @Basic
    @Column(name = "expiration")
    private Timestamp expiration;

    @Basic
    @Column(name = "user_id")
    private Long userId;

    @Basic
    @Column(name = "client_id", nullable = false)
    private String clientId;
}
