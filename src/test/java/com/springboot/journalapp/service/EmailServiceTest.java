package com.springboot.journalapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendEmail_Success() {

        emailService.sendEmail(
                "test@example.com",
                "Test Subject",
                "Test Body"
        );

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(javaMailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals("test@example.com", message.getTo()[0]);
        assertEquals("Test Subject", message.getSubject());
        assertEquals("Test Body", message.getText());
    }

    @Test
    void testSendEmail_Exception() {

        doThrow(new RuntimeException("Mail Server Down"))
                .when(javaMailSender)
                .send(any(SimpleMailMessage.class));

        emailService.sendEmail(
                "test@example.com",
                "Subject",
                "Body"
        );

        verify(javaMailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }
}