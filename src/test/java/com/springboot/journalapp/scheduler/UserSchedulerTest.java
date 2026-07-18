package com.springboot.journalapp.scheduler;

import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.enums.Sentiment;
import com.springboot.journalapp.model.SentimentData;
import com.springboot.journalapp.service.EmailService;
import com.springboot.journalapp.service.UserRepositoryCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSchedulerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepositoryCriteria userRepositoryCriteria;

    @Mock
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @InjectMocks
    private UserScheduler userScheduler;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("test@test.com");
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldSendKafkaMessage_WhenRecentSentimentsExist() {

        JournalEntry entry = new JournalEntry();
        entry.setDate(LocalDateTime.now().minusDays(2));
        entry.setSentiment(Sentiment.HAPPY);

        user.setJournalEntryList(List.of(entry));

        when(userRepositoryCriteria.getUserForSA()).thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(userRepositoryCriteria).getUserForSA();

        ArgumentCaptor<SentimentData> captor =
                ArgumentCaptor.forClass(SentimentData.class);

        verify(kafkaTemplate).send(
                eq("weekly-sentiments"),
                eq("test@test.com"),
                captor.capture()
        );

        SentimentData data = captor.getValue();

        assertEquals("test@test.com", data.getEmail());
        assertEquals("Sentiment for last 7 daysHAPPY", data.getSentiment());

        verifyNoInteractions(emailService);
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldIgnoreOldEntries() {

        JournalEntry oldEntry = new JournalEntry();
        oldEntry.setDate(LocalDateTime.now().minusDays(10));
        oldEntry.setSentiment(Sentiment.HAPPY);

        user.setJournalEntryList(List.of(oldEntry));

        when(userRepositoryCriteria.getUserForSA()).thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verifyNoInteractions(kafkaTemplate);
        verifyNoInteractions(emailService);
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldSendMostFrequentSentiment() {

        JournalEntry e1 = new JournalEntry();
        e1.setDate(LocalDateTime.now().minusDays(1));
        e1.setSentiment(Sentiment.HAPPY);

        JournalEntry e2 = new JournalEntry();
        e2.setDate(LocalDateTime.now().minusDays(2));
        e2.setSentiment(Sentiment.HAPPY);

        JournalEntry e3 = new JournalEntry();
        e3.setDate(LocalDateTime.now().minusDays(3));
        e3.setSentiment(Sentiment.SAD);

        JournalEntry old = new JournalEntry();
        old.setDate(LocalDateTime.now().minusDays(20));
        old.setSentiment(Sentiment.ANGRY);

        user.setJournalEntryList(List.of(e1, e2, e3, old));

        when(userRepositoryCriteria.getUserForSA()).thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        ArgumentCaptor<SentimentData> captor =
                ArgumentCaptor.forClass(SentimentData.class);

        verify(kafkaTemplate).send(
                eq("weekly-sentiments"),
                eq("test@test.com"),
                captor.capture()
        );

        assertEquals(
                "Sentiment for last 7 daysHAPPY",
                captor.getValue().getSentiment()
        );

        verifyNoInteractions(emailService);
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldHandleNoUsers() {

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(Collections.emptyList());

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(userRepositoryCriteria).getUserForSA();
        verifyNoInteractions(kafkaTemplate);
        verifyNoInteractions(emailService);
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldProcessMultipleUsers() {

        UserEntity secondUser = new UserEntity();
        secondUser.setEmail("second@test.com");

        JournalEntry e1 = new JournalEntry();
        e1.setDate(LocalDateTime.now());
        e1.setSentiment(Sentiment.HAPPY);

        JournalEntry e2 = new JournalEntry();
        e2.setDate(LocalDateTime.now());
        e2.setSentiment(Sentiment.SAD);

        user.setJournalEntryList(List.of(e1));
        secondUser.setJournalEntryList(List.of(e2));

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(List.of(user, secondUser));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(kafkaTemplate).send(
                eq("weekly-sentiments"),
                eq("test@test.com"),
                any(SentimentData.class)
        );

        verify(kafkaTemplate).send(
                eq("weekly-sentiments"),
                eq("second@test.com"),
                any(SentimentData.class)
        );

        verify(kafkaTemplate, times(2))
                .send(eq("weekly-sentiments"), anyString(), any(SentimentData.class));

        verifyNoInteractions(emailService);
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldIgnoreNullSentiments() {

        JournalEntry entry = new JournalEntry();
        entry.setDate(LocalDateTime.now());
        entry.setSentiment(null);

        user.setJournalEntryList(List.of(entry));

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verifyNoInteractions(kafkaTemplate);
        verifyNoInteractions(emailService);
    }
}