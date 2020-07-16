package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * user entity
 *
 */
@Entity
@Data
@Cacheable(false)
@EqualsAndHashCode(callSuper = false)
@Table(name = "user", schema = "pauth", catalog = "")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "checkcode")
    private String checkcode;

    @Column(name = "roles")
    private String roles;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_visit_at", nullable = false)
    private Date lastVisitAt;

}
