package com.mjc.school.repository.implementation;

import com.mjc.school.repository.BaseRepository;
import com.mjc.school.repository.filter.Page;
import com.mjc.school.repository.filter.Pagination;
import com.mjc.school.repository.filter.SearchCriteria;
import com.mjc.school.repository.model.BaseEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T extends BaseEntity<K>, K> implements BaseRepository<T, K> {
    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;
    private final Class<K> idClass;

    protected AbstractRepository() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        entityClass = (Class<T>) type.getActualTypeArguments()[0];
        idClass = (Class<K>) type.getActualTypeArguments()[1];
    }

    @Override
    public Page<T> readAll(Pagination pagination, SearchCriteria searchCriteria) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        Predicate predicate = getPredicate(searchCriteria, root, criteriaBuilder);
        criteriaQuery.where(predicate);

        setOrder(pagination, criteriaQuery, root, criteriaBuilder);

        int pageNumber = pagination.getPageNumber();
        int pageSize = pagination.getPageSize();

        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);

        long entitiesCount = getEntitiesCount(predicate, criteriaBuilder);
        List<T> entities = query.getResultList();
        return new Page<>(entities, pageNumber, entitiesCount);
    }

    private Predicate getPredicate(SearchCriteria searchCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (searchCriteria.getFieldName() != null &&
                !searchCriteria.getFieldValue().isEmpty() &&
                searchCriteria.getFieldValue() != null) {

            predicates.add(criteriaBuilder.like(root.get(searchCriteria.getFieldName()), "%" + searchCriteria.getFieldValue() + "%"));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(Pagination pagination, CriteriaQuery<T> criteriaQuery, Root<T> root, CriteriaBuilder criteriaBuilder) {
        if (pagination.getSortDirection() == Pagination.SortDirection.ASC) {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get(pagination.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get(pagination.getSortBy())));
        }
    }

    private long getEntitiesCount(Predicate predicate, CriteriaBuilder criteriaBuilder) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = countQuery.from(entityClass);
        countQuery.select(criteriaBuilder.count(root)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Override
    public Optional<T> readById(K id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public T update(T newEntity) {
        T updated = null;
        if (readById(newEntity.getId()).isPresent()) {
            T entity = readById(newEntity.getId()).get();
            update(entity, newEntity);
            updated = entityManager.merge(entity);
        }
        entityManager.flush();
        return updated;
    }

    public abstract void update(T entity, T newEntity);

    @Override
    public boolean deleteById(K id) {
        T entity = entityManager.getReference(this.entityClass, id);
        entityManager.remove(entity);
        return true;
    }

    @Override
    public boolean existById(K id) {
        EntityType<T> entityType = entityManager.getMetamodel().entity(entityClass);
        String idFieldName = entityType.getId(idClass).getName();

        Query query = entityManager.createQuery("SELECT COUNT(*) FROM " +
                        entityClass.getSimpleName() + " WHERE " + idFieldName + " = ?1")
                .setParameter(1, id);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public T getReference(K id) {
        return entityManager.getReference(this.entityClass, id);
    }
}
