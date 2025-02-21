package com.mjc.school.repository;

import com.mjc.school.repository.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {
    @Override
    Page<Tag> findAll(Pageable pageable);

    @Query("SELECT t FROM Tag t INNER JOIN t.news n WHERE n.id = :newsId")
    Page<Tag> readByNewsId(@Param("newsId") Long newsId, Pageable pageable);
}
