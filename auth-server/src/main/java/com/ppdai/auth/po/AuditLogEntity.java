package com.ppdai.auth.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * audit log entity
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable(false)
@Table(name = "audit_log", schema = "pauth", catalog = "")
public class AuditLogEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "http_uri")
    private String httpUri;

    @Column(name = "class_method")
    private String classMethod;

    @Column(name = "class_method_args")
    private String classMethodArgs;

    @Column(name = "class_method_return")
    private String classMethodReturn;
}
