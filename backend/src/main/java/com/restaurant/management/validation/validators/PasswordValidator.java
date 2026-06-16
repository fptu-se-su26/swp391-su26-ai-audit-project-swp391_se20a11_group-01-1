package com.restaurant.management.validation.validators;

import com.restaurant.management.validation.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        // Placeholder implementation
        // e.g. length >= 8
        return password.length() >= 8;
    }
}
