package com.ppdai.auth.dao;

import com.ppdai.auth.po.ApprovedScopeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * approved scope repository
 *
 */
public interface ApprovedScopeRepository extends CrudRepository<ApprovedScopeEntity, Long> {

    @Query("SELECT e FROM ApprovedScopeEntity e WHERE e.isActive=true and e.approvedSiteId=?1")
    Iterable<ApprovedScopeEntity> findBySiteId(Long siteId);

}
