package com.ppdai.auth.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * approved site entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "approved_site", schema = "pauth", catalog = "")
public class ApprovedSiteEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Basic
    @Column(name = "user_name", nullable = false)
    private String userName;

    @JsonInclude()
    @Transient
    private Iterable<ApprovedScopeEntity> scopes;

}
