package com.ppdai.auth.dao;

import com.ppdai.auth.po.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * user repository
 *
 */
public interface UserRepository extends BaseJpaRepository<UserEntity, Long> {

    Long countByName(String name);

    @Query("SELECT e FROM UserEntity e WHERE e.isActive=true AND e.name=?1")
    UserEntity findOneByName(String name);

    @Query("SELECT e FROM UserEntity e WHERE e.isActive=true AND e.email=?1")
    UserEntity findOneByEmail(String email);

    Page<UserEntity> findAll(Specification<UserEntity> specification, Pageable pageable);

    @Query("select e from UserEntity e where e.isActive=true")
    @Override
    Page<UserEntity> findAll(Pageable pageable);

    @Query("select e from UserEntity e where e.isActive=true and e.name like concat('%',:name,'%')")
    Page<UserEntity> fuzzyFindByName(@Param("name") String name, Pageable pageable);
}
