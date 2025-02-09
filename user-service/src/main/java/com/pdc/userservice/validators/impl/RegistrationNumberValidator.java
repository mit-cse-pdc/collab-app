package com.pdc.userservice.validators.impl;

import com.pdc.userservice.validators.ValidRegistrationNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class RegistrationNumberValidator implements ConstraintValidator<ValidRegistrationNumber, String> {
    private static final String REGISTRATION_PATTERN = "^\\d{9}$";

    @Override
    public boolean isValid(String registrationNo, ConstraintValidatorContext context) {
        if (registrationNo == null || registrationNo.trim().isEmpty()) {
            return false;
        }
        return Pattern.compile(REGISTRATION_PATTERN)
                .matcher(registrationNo)
                .matches();
    }
}
