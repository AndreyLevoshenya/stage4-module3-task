package com.mjc.school.repository;

import com.mjc.school.repository.filter.Page;
import com.mjc.school.repository.filter.Pagination;
import com.mjc.school.repository.filter.SearchCriteria;
import com.mjc.school.repository.model.BaseEntity;

import java.util.Optional;

public interface BaseRepository<T extends BaseEntity<K>, K> {

    Page<T> readAll(Pagination pagination, SearchCriteria searchCriteria);

    Optional<T> readById(K id);

    T create(T entity);

    T update(T entity);

    boolean deleteById(K id);

    boolean existById(K id);

    T getReference(K id);
}
