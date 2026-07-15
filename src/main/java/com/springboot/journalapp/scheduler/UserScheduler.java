package com.springboot.journalapp.scheduler;

import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.enums.Sentiment;
import com.springboot.journalapp.service.EmailService;
import com.springboot.journalapp.service.UserRepositoryCriteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserScheduler {
    private final EmailService emailService;
    private final UserRepositoryCriteria userRepositoryCriteria;

    public UserScheduler(EmailService emailService, UserRepositoryCriteria userRepositoryCriteria) {
        this.emailService = emailService;
        this.userRepositoryCriteria = userRepositoryCriteria;
    }

    @Scheduled(cron = "0 0 9 * * SUN")
//    @Scheduled(cron = "0 * * * * *")
    public void fetchUserAndSendSentimentAnalysisMail() {
        List<UserEntity> users = userRepositoryCriteria.getUserForSA();
        for (UserEntity user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntryList();
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(entry -> entry.getDate().isAfter(LocalDateTime.now().minusDays(7)))
                    .map(JournalEntry::getSentiment)
                    .toList();

           Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
           for (Sentiment sentiment : sentiments) {
               if (sentiment != null)
                   sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
           }

           Sentiment mostFrequentSentiment = null;
           int maxCount = 0;
           for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
               if (entry.getValue() > maxCount) {
                   maxCount = entry.getValue();
                   mostFrequentSentiment = entry.getKey();
               }
           }

           if (mostFrequentSentiment != null) {
               emailService.sendEmail(user.getEmail(), "Sentiment for last 7 days", mostFrequentSentiment.toString());
           }
        }
    }
}
