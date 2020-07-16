package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * client scope entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "client_scope", schema = "pauth", catalog = "")
public class ClientScopeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Basic
    @Column(name = "scope_name", nullable = false)
    private String scopeName;

}
