package com.mjc.school.repository;

import com.mjc.school.repository.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
    @Query("SELECT a FROM Author a INNER JOIN a.news n WHERE n.id = :newsId")
    Optional<Author> readByNewsId(@Param("newsId") Long newsId);
}
