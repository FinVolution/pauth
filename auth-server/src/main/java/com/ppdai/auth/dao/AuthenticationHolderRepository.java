package com.ppdai.auth.dao;

import com.ppdai.auth.po.AuthenticationHolderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * authentication holder repository
 *
 */
public interface AuthenticationHolderRepository extends BaseJpaRepository<AuthenticationHolderEntity, Long> {

}
