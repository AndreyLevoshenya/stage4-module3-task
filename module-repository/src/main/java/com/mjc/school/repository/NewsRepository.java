package com.mjc.school.repository;

import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.SearchParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    @Query("""
                SELECT n FROM News n
                LEFT JOIN n.author a
                LEFT JOIN n.tags t
                WHERE (:#{#params.newsTitle} IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :#{#params.newsTitle}, '%')))
                AND (:#{#params.newsContent} IS NULL OR LOWER(n.content) LIKE LOWER(CONCAT('%', :#{#params.newsContent}, '%')))
                AND (:#{#params.authorName} IS NULL OR LOWER(a.name) = LOWER(:#{#params.authorName}))
                AND (:#{#params.tagIds} IS NULL OR t.id IN (:#{#params.tagIds}))
                AND (:#{#params.tagNames} IS NULL OR LOWER(t.name) IN (:#{#params.tagNames}))
            """)
    Page<News> readByParams(@Param("params") SearchParameters params, Pageable pageable);
}
