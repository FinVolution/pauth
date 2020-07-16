package com.ppdai.auth.dao;

import com.ppdai.auth.po.RefreshTokenEntity;

import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * refresh token repository
 *
 */
public interface RefreshTokenRepository extends BaseJpaRepository<RefreshTokenEntity, Long> {

    @Query("SELECT e FROM RefreshTokenEntity e WHERE e.isActive=true order by e.id desc ")
    Iterable<RefreshTokenEntity> findAllEx();

    @Query("SELECT e FROM RefreshTokenEntity e WHERE e.isActive=true and e.clientId=?1")
    RefreshTokenEntity findByIdEx(Long id);

    @Query("SELECT e FROM RefreshTokenEntity e WHERE e.isActive=true and e.tokenValue=?1")
    RefreshTokenEntity findByTokenEx(String token);

    @Query("SELECT e FROM RefreshTokenEntity e WHERE e.tokenValue=?1")
    RefreshTokenEntity findByToken(String token);

    @Query("select e from RefreshTokenEntity e where e.isActive=true and e.userId=?1 order by e.id desc ")
    Iterable<RefreshTokenEntity> findByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("update RefreshTokenEntity e set e.isActive=false where e.id=?1")
    void removeById(Long id);

    /**
     * 软删除过期的token，设置表字段的删除标记位
     *
     * @param expired 过期时间
     */
    @Modifying(clearAutomatically = true)
    @Query("update RefreshTokenEntity e set e.isActive=false where e.isActive=true and e.expiration<=?1")
    void removeByTimeBefore(Timestamp expired);

    /**
     * 硬删除过期的token，从数据库中永久删除字段
     *
     * @param expired 过期时间
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from RefreshTokenEntity e where e.isActive=false and e.updateTime<=?1")
    void deleteByTimeBefore(Timestamp expired);

    /**
     * 从数据库中找到所有过期失效的token
     *
     * @return
     */
    @Query("SELECT e FROM RefreshTokenEntity e where e.isActive=false and e.updateTime<=?1")
    Iterable<RefreshTokenEntity> findInactiveTokenByTimeBefore(Timestamp expired);

    Page<RefreshTokenEntity> findAll(Specification<RefreshTokenEntity> specification, Pageable pageable);

    @Query("select e from RefreshTokenEntity e where e.isActive=true")
    @Override
    Page<RefreshTokenEntity> findAll(Pageable pageable);
}
