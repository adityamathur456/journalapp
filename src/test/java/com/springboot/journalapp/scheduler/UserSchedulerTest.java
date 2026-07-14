package com.springboot.journalapp.scheduler;

import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.EmailService;
import com.springboot.journalapp.service.SentimentAnalysisService;
import com.springboot.journalapp.service.UserRepositoryCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSchedulerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepositoryCriteria userRepositoryCriteria;

    @Mock
    private SentimentAnalysisService sentimentAnalysisService;

    @InjectMocks
    private UserScheduler userScheduler;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("test@test.com");
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldSendMail_WhenRecentEntriesExist() {

        JournalEntry entry = new JournalEntry();
        entry.setContent("I am happy");
        entry.setDate(LocalDateTime.now().minusDays(2));

        user.setJournalEntryList(List.of(entry));

        when(userRepositoryCriteria.getUserForSA()).thenReturn(List.of(user));
        when(sentimentAnalysisService.getSentiment("I am happy"))
                .thenReturn("Positive");

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(userRepositoryCriteria).getUserForSA();
        verify(sentimentAnalysisService).getSentiment("I am happy");
        verify(emailService)
                .sendEmail("test@test.com", "Sentiment for 7 days", "Positive");
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldIgnoreOldEntries() {

        JournalEntry entry = new JournalEntry();
        entry.setContent("Old Entry");
        entry.setDate(LocalDateTime.now().minusDays(10));

        user.setJournalEntryList(List.of(entry));

        when(userRepositoryCriteria.getUserForSA()).thenReturn(List.of(user));
        when(sentimentAnalysisService.getSentiment("")).thenReturn("");

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(sentimentAnalysisService).getSentiment("");
        verify(emailService)
                .sendEmail("test@test.com", "Sentiment for 7 days", "");
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldConcatenateRecentEntries() {

        JournalEntry entry1 = new JournalEntry();
        entry1.setContent("Good");
        entry1.setDate(LocalDateTime.now().minusDays(1));

        JournalEntry entry2 = new JournalEntry();
        entry2.setContent("Bad");
        entry2.setDate(LocalDateTime.now().minusDays(3));

        JournalEntry oldEntry = new JournalEntry();
        oldEntry.setContent("Ignore");
        oldEntry.setDate(LocalDateTime.now().minusDays(20));

        user.setJournalEntryList(List.of(entry1, entry2, oldEntry));

        when(userRepositoryCriteria.getUserForSA()).thenReturn(List.of(user));
        when(sentimentAnalysisService.getSentiment(anyString()))
                .thenReturn("Mixed");

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(sentimentAnalysisService).getSentiment(captor.capture());

        assertEquals("Good Bad", captor.getValue());

        verify(emailService)
                .sendEmail("test@test.com", "Sentiment for 7 days", "Mixed");
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldHandleNoUsers() {

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(Collections.emptyList());

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(userRepositoryCriteria).getUserForSA();
        verifyNoInteractions(sentimentAnalysisService);
        verifyNoInteractions(emailService);
    }

    @Test
    void fetchUserAndSendSentimentAnalysisMail_ShouldProcessMultipleUsers() {

        UserEntity user2 = new UserEntity();
        user2.setEmail("second@test.com");

        JournalEntry e1 = new JournalEntry();
        e1.setContent("First");
        e1.setDate(LocalDateTime.now());

        JournalEntry e2 = new JournalEntry();
        e2.setContent("Second");
        e2.setDate(LocalDateTime.now());

        user.setJournalEntryList(List.of(e1));
        user2.setJournalEntryList(List.of(e2));

        when(userRepositoryCriteria.getUserForSA())
                .thenReturn(List.of(user, user2));

        when(sentimentAnalysisService.getSentiment(anyString()))
                .thenReturn("Positive");

        userScheduler.fetchUserAndSendSentimentAnalysisMail();

        verify(emailService, times(2))
                .sendEmail(anyString(), eq("Sentiment for 7 days"), eq("Positive"));

        verify(sentimentAnalysisService, times(2))
                .getSentiment(anyString());
    }
}