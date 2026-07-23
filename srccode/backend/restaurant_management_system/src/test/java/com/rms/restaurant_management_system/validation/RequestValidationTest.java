package com.rms.restaurant_management_system.validation;

import com.rms.restaurant_management_system.dto.request.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidationTest {
    static Validator validator;

    @BeforeAll static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void registrationRequiresUsernameValidEmailAndPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(" "); request.setEmail("not-an-email"); request.setPassword("");
        assertEquals(Set.of("username", "email", "password"), invalidFields(request));
    }

    @Test
    void loginRequiresValidEmailAndPassword() {
        LoginRequest request = new LoginRequest(); request.setEmail("bad");
        assertEquals(Set.of("email", "password"), invalidFields(request));
    }

    @Test
    void resetPasswordRequiresSixCharacterPasswordAndOtp() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("user@example.com"); request.setOtp(""); request.setNewPassword("12345");
        assertEquals(Set.of("otp", "newPassword"), invalidFields(request));
    }

    @Test
    void reservationEnforcesRequiredFieldsAndGuestBounds() {
        ReservationRequest request = new ReservationRequest();
        request.setReservationDate(LocalDate.now().plusDays(1)); request.setReservationTime("18:00");
        request.setCustomerName("Lan"); request.setCustomerPhone("0900000000"); request.setNumberOfGuests(0);
        assertEquals(Set.of("numberOfGuests"), invalidFields(request));
        request.setNumberOfGuests(21);
        assertEquals(Set.of("numberOfGuests"), invalidFields(request));
        request.setNumberOfGuests(4);
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void orderRequiresAtLeastOneValidItem() {
        OrderRequest order = new OrderRequest(); order.setItems(List.of());
        assertEquals(Set.of("items"), invalidFields(order));
        OrderItemRequest item = new OrderItemRequest(); item.setFoodId(1L); item.setQuantity(0);
        order.setItems(List.of(item));
        assertEquals(Set.of("items[0].quantity"), invalidPaths(order));
    }

    @Test
    void feedbackRatingMustBeBetweenOneAndFiveAndContentRequired() {
        FeedbackRequest request = new FeedbackRequest(); request.setRating(0); request.setContent(" ");
        assertEquals(Set.of("rating", "content"), invalidFields(request));
        request.setRating(6);
        assertEquals(Set.of("rating", "content"), invalidFields(request));
    }

    @Test
    void tableRequiresPositiveCapacityAndName() {
        TableRequest request = new TableRequest(); request.setTableName(""); request.setCapacity(0);
        assertEquals(Set.of("tableName", "capacity"), invalidFields(request));
    }

    private Set<String> invalidFields(Object value) {
        return validator.validate(value).stream().map(v -> v.getPropertyPath().toString().split("\\[")[0]).collect(java.util.stream.Collectors.toSet());
    }

    private Set<String> invalidPaths(Object value) {
        return validator.validate(value).stream().map(v -> v.getPropertyPath().toString()).collect(java.util.stream.Collectors.toSet());
    }
}
