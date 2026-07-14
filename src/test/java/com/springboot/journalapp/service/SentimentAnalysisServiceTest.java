package com.springboot.journalapp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SentimentAnalysisServiceTest {

    private final SentimentAnalysisService sentimentAnalysisService = new SentimentAnalysisService();

    @Test
    void getSentiment_ShouldReturnSameEntry() {
        String entry = "Today was a wonderful day.";

        String result = sentimentAnalysisService.getSentiment(entry);

        assertEquals(entry, result);
    }

    @Test
    void getSentiment_ShouldReturnEmptyString() {
        String result = sentimentAnalysisService.getSentiment("");

        assertEquals("", result);
    }

    @Test
    void getSentiment_ShouldReturnNull_WhenInputIsNull() {
        assertNull(sentimentAnalysisService.getSentiment(null));
    }
}