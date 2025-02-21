package com.mjc.school.repository;

import com.mjc.school.repository.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    @Query("SELECT c FROM Comment c WHERE c.news.id = :newsId")
    Page<Comment> readByNewsId(@Param("newsId") Long newsId, Pageable pageable);
}
