package com.springboot.journalapp.scheduler;

import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.enums.Sentiment;
import com.springboot.journalapp.service.EmailService;
import com.springboot.journalapp.service.UserRepositoryCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSchedulerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepositoryCriteria userRepositoryCriteria;

    @InjectMocks
    private UserScheduler userScheduler;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("test@test.com");
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldSendMail_WhenRecentSentimentsExist() {

        JournalEntry entry = new JournalEntry();
        entry.setDate(LocalDateTime.now().minusDays(2));
        entry.setSentiment(Sentiment.HAPPY);

        user.setJournalEntryList(List.of(entry));

        when(userRepositoryCriteria.getUserForSA()).thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(userRepositoryCriteria).getUserForSA();
        verify(emailService).sendEmail(
                "test@test.com",
                "Sentiment for last 7 days",
                "HAPPY"
        );
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldIgnoreOldEntries() {

        JournalEntry oldEntry = new JournalEntry();
        oldEntry.setDate(LocalDateTime.now().minusDays(10));
        oldEntry.setSentiment(Sentiment.HAPPY);

        user.setJournalEntryList(List.of(oldEntry));

        when(userRepositoryCriteria.getUserForSA()).thenReturn(List.of(user));

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

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

        verify(emailService).sendEmail(
                "test@test.com",
                "Sentiment for last 7 days",
                "HAPPY"
        );
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldHandleNoUsers() {

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(Collections.emptyList());

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(userRepositoryCriteria).getUserForSA();
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

        verify(emailService).sendEmail(
                "test@test.com",
                "Sentiment for last 7 days",
                "HAPPY"
        );

        verify(emailService).sendEmail(
                "second@test.com",
                "Sentiment for last 7 days",
                "SAD"
        );

        verify(emailService, times(2))
                .sendEmail(anyString(), eq("Sentiment for last 7 days"), anyString());
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

        verifyNoInteractions(emailService);
    }
}