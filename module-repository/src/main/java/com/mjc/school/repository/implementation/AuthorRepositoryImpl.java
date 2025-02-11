package com.mjc.school.repository.implementation;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.model.Author;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthorRepositoryImpl extends AbstractRepository<Author, Long> implements AuthorRepository {

    @Override
    public void update(Author entity, Author newEntity) {
        entity.setName(newEntity.getName());
    }

    @Override
    public Optional<Author> readByNewsId(Long newsId) {
        TypedQuery<Author> query = entityManager.createQuery("SELECT a FROM Author a " +
                        "INNER JOIN a.news n WHERE n.id=:newsId", Author.class)
                .setParameter("newsId", newsId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
