package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * approved scope entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "approved_site_scope", schema = "pauth", catalog = "")
public class ApprovedScopeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "approved_site_id", nullable = false)
    private Long approvedSiteId;

    @Basic
    @Column(name = "scope_name", nullable = false)
    private String scopeName;

}
