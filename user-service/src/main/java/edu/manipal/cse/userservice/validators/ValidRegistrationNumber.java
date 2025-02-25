package edu.manipal.cse.userservice.validators;

import edu.manipal.cse.userservice.validators.impl.RegistrationNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegistrationNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegistrationNumber {
    String message() default "Registration number must be exactly 9 digits";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
