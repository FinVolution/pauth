package com.ppdai.auth.dao;

import com.ppdai.auth.po.AccessTokenEntity;
import com.ppdai.auth.po.RefreshTokenEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

/**
 * access token repository
 *
 */
public interface AccessTokenRepository extends BaseJpaRepository<AccessTokenEntity, Long> {

    @Query("SELECT e FROM AccessTokenEntity e WHERE e.isActive=true order by e.id desc ")
    Iterable<AccessTokenEntity> findAllEx();

    @Query("SELECT e FROM AccessTokenEntity e WHERE e.isActive=true and e.id=?1")
    AccessTokenEntity findByIdEx(Long id);

    @Query("SELECT e FROM AccessTokenEntity e WHERE e.isActive=true and e.tokenValue=?1")
    AccessTokenEntity findByTokenEx(String token);

    @Query("SELECT e FROM AccessTokenEntity e WHERE e.tokenValue=?1")
    AccessTokenEntity findByToken(String token);

    @Query("select e from AccessTokenEntity e where e.isActive=true and e.refreshToken=?1")
    Iterable<AccessTokenEntity> findByRefreshToken(RefreshTokenEntity refreshToken);

    @Query("select e from AccessTokenEntity e where e.isActive=true and e.userId=?1 order by e.id desc ")
    Iterable<AccessTokenEntity> findByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("update AccessTokenEntity e set e.isActive=false where e.id=?1")
    void removeById(Long id);

    /**
     * 软删除过期的token，设置表字段的删除标记位
     *
     * @param expired 过期时间
     */
    @Modifying(clearAutomatically = true)
    @Query("update AccessTokenEntity e set e.isActive=false where e.isActive=true and e.expiration<=?1")
    void removeByTimeBefore(Timestamp expired);

    /**
     * 硬删除过期的token，从数据库中永久删除字段
     *
     * @param expired 过期时间
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from AccessTokenEntity e where e.isActive=false and e.updateTime<=?1")
    void deleteByTimeBefore(Timestamp expired);

    Page<AccessTokenEntity> findAll(Specification<AccessTokenEntity> specification, Pageable pageable);

    @Query("select e from AccessTokenEntity e where e.isActive=true order by e.id desc ")
    @Override
    Page<AccessTokenEntity> findAll(Pageable pageable);
}
