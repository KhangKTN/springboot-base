package com.khangktn.springbase.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateOfBirthValidator implements ConstraintValidator<DateOfBirthConstrain, LocalDate> {
    private int min;

    @Override
    public boolean isValid(final LocalDate localDateInput, final ConstraintValidatorContext context) {
        if (Objects.isNull(localDateInput)) {
            return true;
        }
        final long yearOld =  ChronoUnit.YEARS.between(localDateInput, LocalDate.now());
        return yearOld >= min;
    }

    @Override
    public void initialize(final DateOfBirthConstrain constrainAnotation) {
        ConstraintValidator.super.initialize(constrainAnotation);
        min = constrainAnotation.min();
    }
}
