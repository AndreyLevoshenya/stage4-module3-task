package com.mjc.school.service.annotations;

import java.lang.annotation.*;

@Documented
@Constraint
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sort {
}
