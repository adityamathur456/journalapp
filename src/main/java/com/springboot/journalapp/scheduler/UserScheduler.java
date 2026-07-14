package com.springboot.journalapp.scheduler;

import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.EmailService;
import com.springboot.journalapp.service.SentimentAnalysisService;
import com.springboot.journalapp.service.UserRepositoryCriteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserScheduler {
    private final EmailService emailService;
    private final UserRepositoryCriteria userRepositoryCriteria;
    private final SentimentAnalysisService sentimentAnalysisService;

    public UserScheduler(EmailService emailService, UserRepositoryCriteria userRepositoryCriteria, SentimentAnalysisService sentimentAnalysisService) {
        this.emailService = emailService;
        this.userRepositoryCriteria = userRepositoryCriteria;
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    @Scheduled(cron = "0 0 9 * * SUN")
//    @Scheduled(cron = "0 * * * * *")
    public void fetchUserAndSendSentimentAnalysisMail() {
        List<UserEntity> users = userRepositoryCriteria.getUserForSA();
        for (UserEntity user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntryList();
            List<String> filteredEntries = journalEntries.stream()
                    .filter(entry -> entry.getDate().isAfter(LocalDateTime.now().minusDays(7)))
                    .map(JournalEntry::getContent)
                    .toList();
            String entries = String.join(" ", filteredEntries);
            String sentiment = sentimentAnalysisService.getSentiment(entries);
            emailService.sendEmail(user.getEmail(), "Sentiment for 7 days", sentiment);
        }
    }
}
