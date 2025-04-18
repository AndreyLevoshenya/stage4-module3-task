package com.mjc.school.repository.filter;

import com.mjc.school.repository.model.BaseEntity;
import org.springframework.data.jpa.domain.Specification;

public class EntitySpecification<T extends BaseEntity<Long>> {
    private static final String PERCENTAGE_SYMBOL = "%";

    public static <T> Specification<T> searchByField(String field, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isEmpty() || field == null || field.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get(field)), PERCENTAGE_SYMBOL + value.toLowerCase() + PERCENTAGE_SYMBOL);
        };
    }
}
