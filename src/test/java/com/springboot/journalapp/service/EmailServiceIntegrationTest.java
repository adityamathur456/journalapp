package com.springboot.journalapp.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendEmail() {
        emailService.sendEmail(
                "realmail@gmail.com",
                "Test Subject",
                "Hello from Spring Boot!"
        );
    }
}