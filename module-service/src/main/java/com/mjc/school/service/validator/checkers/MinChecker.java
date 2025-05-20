package com.mjc.school.service.validator.checkers;

import com.mjc.school.service.annotation.Min;
import org.springframework.stereotype.Component;

@Component
public class MinChecker implements ConstraintChecker<Min> {
    @Override
    public boolean check(Object value, Min constraint) {
        if (value == null) {
            return true;
        }
        if (value instanceof Number number) {
            return number.longValue() >= constraint.value();
        } else {
            return false;
        }
    }

    @Override
    public Class<Min> getType() {
        return Min.class;
    }
}
