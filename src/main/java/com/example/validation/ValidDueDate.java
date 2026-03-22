package com.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DueDateValidator.class)
@Documented
public @interface ValidDueDate {
    String message() default "Due date cannot be before creation date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}