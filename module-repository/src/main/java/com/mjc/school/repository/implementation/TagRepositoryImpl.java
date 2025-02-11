package com.mjc.school.repository.implementation;

import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.model.Tag;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl extends AbstractRepository<Tag, Long> implements TagRepository {

    @Override
    public void update(Tag entity, Tag newEntity) {
        entity.setName(newEntity.getName());
    }

    @Override
    public List<Tag> readByNewsId(Long newsId) {
        TypedQuery<Tag> query = entityManager.createQuery("SELECT t FROM Tag t " +
                        "INNER JOIN t.news n WHERE n.id=:newsId", Tag.class)
                .setParameter("newsId", newsId);
        return query.getResultList();

    }

}
