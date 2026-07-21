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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSchedulerTest {

    @Mock
    private UserRepositoryCriteria userRepositoryCriteria;

    @Mock
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Mock
    private EmailService emailService;

    private UserScheduler userScheduler;

    @BeforeEach
    void setUp() {
        userScheduler = new UserScheduler(userRepositoryCriteria, kafkaTemplate, emailService);
    }

    @Test
    void shouldSendKafkaMessageForMostFrequentSentiment() {

        UserEntity user = new UserEntity();
        user.setEmail("test@gmail.com");

        JournalEntry e1 = new JournalEntry();
        e1.setDate(LocalDateTime.now().minusDays(1));
        e1.setSentiment(Sentiment.HAPPY);

        JournalEntry e2 = new JournalEntry();
        e2.setDate(LocalDateTime.now().minusDays(2));
        e2.setSentiment(Sentiment.HAPPY);

        JournalEntry e3 = new JournalEntry();
        e3.setDate(LocalDateTime.now().minusDays(3));
        e3.setSentiment(Sentiment.SAD);

        user.setJournalEntryList(List.of(e1, e2, e3));

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        ArgumentCaptor<SentimentData> captor =
                ArgumentCaptor.forClass(SentimentData.class);

        verify(kafkaTemplate).send(
                eq("weekly-sentiments"),
                eq("test@gmail.com"),
                captor.capture()
        );

        SentimentData data = captor.getValue();

        assertEquals("test@gmail.com", data.getEmail());
        assertTrue(data.getSentiment().contains("HAPPY"));
    }

    @Test
    void shouldIgnoreOldJournalEntries() {

        UserEntity user = new UserEntity();
        user.setEmail("old@gmail.com");

        JournalEntry oldEntry = new JournalEntry();
        oldEntry.setDate(LocalDateTime.now().minusDays(10));
        oldEntry.setSentiment(Sentiment.HAPPY);

        user.setJournalEntryList(List.of(oldEntry));

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(kafkaTemplate, never())
                .send(anyString(), anyString(), any(SentimentData.class));
    }

    @Test
    void shouldIgnoreNullSentiments() {

        UserEntity user = new UserEntity();
        user.setEmail("null@gmail.com");

        JournalEntry entry = new JournalEntry();
        entry.setDate(LocalDateTime.now().minusDays(1));
        entry.setSentiment(null);

        user.setJournalEntryList(List.of(entry));

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(kafkaTemplate, never())
                .send(anyString(), anyString(), any(SentimentData.class));
    }

    @Test
    void shouldHandleEmptyUserList() {

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(List.of());

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void shouldSendOnlyForUserHavingRecentSentiment() {

        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@gmail.com");

        JournalEntry recent = new JournalEntry();
        recent.setDate(LocalDateTime.now().minusDays(2));
        recent.setSentiment(Sentiment.SAD);

        user1.setJournalEntryList(List.of(recent));

        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@gmail.com");

        JournalEntry old = new JournalEntry();
        old.setDate(LocalDateTime.now().minusDays(20));
        old.setSentiment(Sentiment.HAPPY);

        user2.setJournalEntryList(List.of(old));

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(List.of(user1, user2));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(kafkaTemplate, times(1))
                .send(eq("weekly-sentiments"),
                        eq("user1@gmail.com"),
                        any(SentimentData.class));
    }
}