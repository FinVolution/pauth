package com.ppdai.auth.dao;

import com.ppdai.auth.po.UserSecurityActionEntity;
import org.springframework.data.jpa.repository.Query;

/**
 * security action repository
 *
 */
public interface SecurityActionRepository extends BaseJpaRepository<UserSecurityActionEntity, Long> {

    @Query("SELECT e FROM UserSecurityActionEntity e WHERE e.isActive=true AND e.onceFlag=?1")
    UserSecurityActionEntity findAction(String onceFlag);

    @Query("SELECT e FROM UserSecurityActionEntity e WHERE e.isActive=true AND e.onceFlag=?1 AND e.userId=?2")
    UserSecurityActionEntity findAction(String onceFlag, Long userId);

}
