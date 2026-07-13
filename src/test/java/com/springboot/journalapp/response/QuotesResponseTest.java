package com.springboot.journalapp.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuotesResponseTest {

    @Test
    void testQuotesResponse() {

        QuotesResponse response = new QuotesResponse();

        response.setQuote("The only limit is your mind.");
        response.setAuthor("Anonymous");
        response.setWork("Unknown");
        response.setCategories(List.of("life", "motivation"));

        assertEquals("The only limit is your mind.",
                response.getQuote());

        assertEquals("Anonymous",
                response.getAuthor());

        assertEquals("Unknown",
                response.getWork());

        assertEquals(List.of("life", "motivation"),
                response.getCategories());
    }
}