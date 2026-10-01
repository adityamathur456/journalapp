package com.springboot.journalapp.scheduler;

import com.springboot.journalapp.entity.JournalEntry;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.enums.Sentiment;
import com.springboot.journalapp.model.SentimentData;
import com.springboot.journalapp.service.EmailService;
import com.springboot.journalapp.service.UserRepositoryCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserScheduler {
    private final UserRepositoryCriteria userRepositoryCriteria;

    private final EmailService emailService;

    private final KafkaTemplate<String, SentimentData> kafkaTemplate;

    public UserScheduler(UserRepositoryCriteria userRepositoryCriteria, KafkaTemplate<String, SentimentData> kafkaTemplate, EmailService emailService) {
        this.userRepositoryCriteria = userRepositoryCriteria;
        this.kafkaTemplate = kafkaTemplate;
        this.emailService = emailService;
    }

//    @Scheduled(cron = "0 0 9 * * SUN") // This cron sends emails in weekly
    @Scheduled(cron = "0 * * * * *") // This cron sends emails in 1 minute i used it for kafka testing.
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
               SentimentData sentimentData = SentimentData.builder().email(user.getEmail()).sentiment("Sentiment for last 7 days" + mostFrequentSentiment).build();
               try {
                   kafkaTemplate
                           .send("weekly-sentiments", sentimentData.getEmail(), sentimentData)
                           .whenComplete((result, exception) -> {
                               if (exception != null) {
                                   emailService.sendEmail(
                                           sentimentData.getEmail(),
                                           "weekly-sentiments",
                                           sentimentData.getSentiment()
                                   );
                               }
                           });
               } catch (Exception e) {
                   log.error("Error while processing weekly sentiment email", e);
               }
           }
        }
    }
}
