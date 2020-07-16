package com.ppdai.auth.dao;

import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.po.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * client repository
 *
 */
public interface ClientRepository extends BaseJpaRepository<ClientEntity, Long> {

    @Query("SELECT e FROM ClientEntity e WHERE e.isActive=true")
    List<ClientEntity> findAllEx();

    @Query("SELECT e FROM ClientEntity e WHERE e.isActive=true and e.owner=?1")
    List<ClientEntity> findByOwner(UserEntity owner);

    @Query("SELECT e FROM ClientEntity e WHERE e.isActive=true and e.clientId=?1")
    ClientEntity findByIdEx(String clientId);

    @Query("SELECT e FROM ClientEntity e WHERE e.clientId=?1")
    ClientEntity findByClientId(String clientId);

    @Modifying(clearAutomatically = true)
    @Query("update ClientEntity e set e.isActive=false where e.id=?1")
    void removeById(Long id);

    Page<ClientEntity> findAll(Specification<ClientEntity> specification, Pageable pageable);

    @Query("select e from ClientEntity e where e.isActive=true")
    @Override
    Page<ClientEntity> findAll(Pageable pageable);

}
