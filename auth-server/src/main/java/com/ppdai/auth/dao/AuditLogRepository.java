package com.ppdai.auth.dao;

import com.ppdai.auth.po.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;

/**
 * audit log repository
 *
 */
public interface AuditLogRepository extends CrudRepository<AuditLogEntity, Long> {

    /**
     * 删除过期的审计日志，从数据库中永久删除字段
     *
     * @param expired 过期时间
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from AuditLogEntity e where e.updateTime<=?1")
    void deleteByTimeBefore(Timestamp expired);

    @Query("select e from AuditLogEntity e where e.isActive=true and e.userName is not null order by e.id desc")
    Page<AuditLogEntity> findAll(Pageable pageable);
}
