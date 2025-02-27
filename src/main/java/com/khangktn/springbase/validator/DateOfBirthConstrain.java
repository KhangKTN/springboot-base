package com.khangktn.springbase.validator;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ FIELD }) // Validate will apply for field, class
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { DateOfBirthValidator.class })
public @interface DateOfBirthConstrain {
    String message() default "Invalid Date of birth";

    int min();

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
