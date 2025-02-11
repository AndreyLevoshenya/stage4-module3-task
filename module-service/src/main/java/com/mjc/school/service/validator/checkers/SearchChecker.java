package com.mjc.school.service.validator.checkers;

import com.mjc.school.service.annotations.Search;
import org.springframework.stereotype.Component;

@Component
public class SearchChecker implements ConstraintChecker<Search> {
    @Override
    public boolean check(Object value, Search constraint) {
        if (value instanceof CharSequence sequence) {
            return (!sequence.isEmpty() && sequence.toString().matches("^[\\w_]+:.*$"));
        }
        return true;
    }

    @Override
    public Class<Search> getType() {
        return Search.class;
    }
}
