package com.springboot.journalapp.service;

import com.springboot.journalapp.model.SentimentData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SentimentConsumerServiceTest {

    @Mock
    private EmailService emailService;

    private SentimentConsumerService sentimentConsumerService;

    @BeforeEach
    void setUp() {
        sentimentConsumerService = new SentimentConsumerService(emailService);
    }

    @Test
    void consume_ShouldSendEmail() {

        SentimentData sentimentData = SentimentData.builder()
                .email("test@gmail.com")
                .sentiment("Sentiment for last 7 days HAPPY")
                .build();

        sentimentConsumerService.consume(sentimentData);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                bodyCaptor.capture()
        );

        assertEquals("test@gmail.com", emailCaptor.getValue());
        assertEquals("Sentiment for previous week", subjectCaptor.getValue());
        assertEquals("Sentiment for last 7 days HAPPY", bodyCaptor.getValue());
    }
}