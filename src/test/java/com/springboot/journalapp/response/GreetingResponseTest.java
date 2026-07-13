package com.springboot.journalapp.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GreetingResponseTest {

    @Test
    void testGreetingResponse() {

        WeatherResponse.Current current = new WeatherResponse.Current();
        current.setObservationTime("10:00 AM");
        current.setTemperature(30);

        QuotesResponse quote = new QuotesResponse();
        quote.setQuote("Stay hungry, stay foolish.");
        quote.setAuthor("Steve Jobs");
        quote.setWork("Stanford Commencement");
        quote.setCategories(List.of("success", "motivation"));

        GreetingResponse response = new GreetingResponse();
        response.setWeather(current);
        response.setQuote(quote);

        assertNotNull(response.getWeather());
        assertNotNull(response.getQuote());

        assertEquals("10:00 AM",
                response.getWeather().getObservationTime());
        assertEquals(30,
                response.getWeather().getTemperature());

        assertEquals("Stay hungry, stay foolish.",
                response.getQuote().getQuote());
        assertEquals("Steve Jobs",
                response.getQuote().getAuthor());
        assertEquals("Stanford Commencement",
                response.getQuote().getWork());
        assertEquals(List.of("success", "motivation"),
                response.getQuote().getCategories());
    }
}