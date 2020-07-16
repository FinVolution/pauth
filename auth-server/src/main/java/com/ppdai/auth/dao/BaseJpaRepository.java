package com.ppdai.auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * a base jpa repository
 *
 */
@NoRepositoryBean
public interface BaseJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Iterable<T> findByIsActiveIsTrue();

    default Iterable<T> getAll() {
        return findByIsActiveIsTrue();
    }

}