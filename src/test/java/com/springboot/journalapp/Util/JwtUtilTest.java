package com.springboot.journalapp.Util;


import com.springboot.journalapp.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET_KEY =
            "0123456789012345678901234567890123456789012345678901234567890123";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", SECRET_KEY);
    }

    @Test
    void generateToken_ShouldReturnToken() {
        String token = jwtUtil.generateToken("john");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUserName_ShouldReturnCorrectUserName() {
        String token = jwtUtil.generateToken("john");

        String username = jwtUtil.extractUserName(token);

        assertEquals("john", username);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        String token = jwtUtil.generateToken("john");

        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtUtil.generateToken("john");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void generateToken_ShouldGenerateDifferentTokens() {
        String token1 = jwtUtil.generateToken("alic");
        String token2 = jwtUtil.generateToken("john");

        assertNotEquals(token1, token2);
    }
}