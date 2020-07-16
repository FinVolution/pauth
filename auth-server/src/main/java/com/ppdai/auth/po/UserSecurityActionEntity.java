package com.ppdai.auth.po;

import com.ppdai.auth.constant.SecurityActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * user security action
 *
 */
@Entity
@Data
@Cacheable(false)
@EqualsAndHashCode(callSuper = false)
@Table(name = "user_security_action", schema = "pauth", catalog = "")
public class UserSecurityActionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SecurityActionType type;

    @Column(name = "once_flag", nullable = false)
    private String onceFlag;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

}
