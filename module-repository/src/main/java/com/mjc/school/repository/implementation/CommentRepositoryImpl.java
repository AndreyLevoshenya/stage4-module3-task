package com.mjc.school.repository.implementation;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.model.Comment;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl extends AbstractRepository<Comment, Long> implements CommentRepository {
    @Override
    public void update(Comment entity, Comment newEntity) {
        entity.setContent(newEntity.getContent());
        entity.setNews(newEntity.getNews());
        entity.setLastUpdateDate(newEntity.getLastUpdateDate());
    }

    @Override
    public List<Comment> readByNewsId(Long newsId) {
        TypedQuery<Comment> query = entityManager.createQuery("SELECT c FROM Comment c" +
                        " WHERE c.news.id=:newsId", Comment.class)
                .setParameter("newsId", newsId);
        return query.getResultList();
    }
}
