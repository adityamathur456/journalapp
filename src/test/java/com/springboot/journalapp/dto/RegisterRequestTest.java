package com.springboot.journalapp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        RegisterRequest request = new RegisterRequest(
                "peter",
                "test@123",
                "peter@gmail.com",
                true
        );

        assertEquals("peter", request.getUserName());
        assertEquals("test@123", request.getPassword());
        assertEquals("peter@gmail.com", request.getEmail());
        assertTrue(request.isSentimentAnalysis());
    }

    @Test
    void testSetters() {
        RegisterRequest request = new RegisterRequest();

        request.setUserName("john");
        request.setPassword("password");
        request.setEmail("john@gmail.com");
        request.setSentimentAnalysis(false);

        assertEquals("john", request.getUserName());
        assertEquals("password", request.getPassword());
        assertEquals("john@gmail.com", request.getEmail());
        assertFalse(request.isSentimentAnalysis());
    }

    @Test
    void testValidation_WhenUserNameIsBlank() {
        RegisterRequest request = new RegisterRequest(
                "",
                "password",
                null,
                true
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidation_WhenPasswordIsBlank() {
        RegisterRequest request = new RegisterRequest(
                "john",
                "",
                null,
                true
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidation_WhenRequestIsValid() {
        RegisterRequest request = new RegisterRequest(
                "john",
                "password",
                null,
                false
        );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}