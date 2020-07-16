package com.ppdai.auth.dao;

import com.ppdai.auth.po.ApprovedSiteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * approved site repository
 *
 */
public interface ApprovedSiteRepository extends BaseJpaRepository<ApprovedSiteEntity, Long> {

    @Query("SELECT e FROM ApprovedSiteEntity e WHERE e.isActive=true and e.userName=?1")
    Iterable<ApprovedSiteEntity> findByUserName(String userName);

    @Modifying(clearAutomatically = true)
    @Query("update ApprovedSiteEntity e set e.isActive=false where e.id=?1")
    void removeById(Long id);

    @Query("SELECT e FROM ApprovedSiteEntity e WHERE e.isActive=true and e.userName=?1 and e.clientId=?2")
    Iterable<ApprovedSiteEntity> findByUserNameAndClientId(String userName, String clientId);

    Page<ApprovedSiteEntity> findAll(Specification<ApprovedSiteEntity> specification, Pageable pageable);
}
