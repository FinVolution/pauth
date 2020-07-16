package com.ppdai.auth.dao;

import com.ppdai.auth.po.AuthorizationCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

/**
 * authorization code repository
 *
 */
public interface AuthorizationCodeRepository extends BaseJpaRepository<AuthorizationCodeEntity, Long> {

    @Query("SELECT e FROM AuthorizationCodeEntity e WHERE e.isActive=true and e.code=?1")
    AuthorizationCodeEntity findByCode(String code);

    @Query("SELECT e FROM AuthorizationCodeEntity e WHERE e.isActive=true")
    Iterable<AuthorizationCodeEntity> findAllEx();

    @Query("select e from AuthorizationCodeEntity e where e.isActive=true and e.userId=?1 order by e.id desc ")
    Iterable<AuthorizationCodeEntity> findByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("update AuthorizationCodeEntity e set e.isActive=false where e.id=?1")
    void removeById(Long id);

    /**
     * 软删除过期的token，设置表字段的删除标记位
     *
     * @param expired 过期时间
     */
    @Modifying(clearAutomatically = true)
    @Query("update AuthorizationCodeEntity e set e.isActive=false where e.isActive=true and e.expiration<=?1")
    void removeByTimeBefore(Timestamp expired);

    /**
     * 硬删除过期的token，从数据库中永久删除字段
     *
     * @param expired 过期时间
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from AuthorizationCodeEntity e where e.isActive=false and e.updateTime<=?1")
    void deleteByTimeBefore(Timestamp expired);

    Page<AuthorizationCodeEntity> findAll(Specification<AuthorizationCodeEntity> specification, Pageable pageable);

    @Query("select e from AuthorizationCodeEntity e where e.isActive=true order by e.id desc ")
    @Override
    Page<AuthorizationCodeEntity> findAll(Pageable pageable);
}
