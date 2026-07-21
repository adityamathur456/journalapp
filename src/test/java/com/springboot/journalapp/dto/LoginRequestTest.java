package com.springboot.journalapp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        LoginRequest request = new LoginRequest(
                "aditya",
                "test@123"
        );

        assertEquals("aditya", request.getUserName());
        assertEquals("test@123", request.getPassword());
    }

    @Test
    void testSetters() {
        LoginRequest request = new LoginRequest();

        request.setUserName("john");
        request.setPassword("password");

        assertEquals("john", request.getUserName());
        assertEquals("password", request.getPassword());
    }

    @Test
    void testValidation_WhenUserNameIsBlank() {
        LoginRequest request = new LoginRequest(
                "",
                "password"
        );

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidation_WhenPasswordIsBlank() {
        LoginRequest request = new LoginRequest(
                "john",
                ""
        );

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidation_WhenRequestIsValid() {
        LoginRequest request = new LoginRequest(
                "john",
                "password"
        );

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}