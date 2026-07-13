package com.springboot.journalapp.service;

import com.springboot.journalapp.response.QuotesResponse;
import com.springboot.journalapp.cache.AppCache;
import com.springboot.journalapp.enums.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotesServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppCache appCache;

    @InjectMocks
    private QuotesService quotesService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(quotesService, "apiKey", "dummy-api-key");

        when(appCache.getValue(Keys.QUOTES_API))
                .thenReturn("https://api.api-ninjas.com/v1/quotes");
    }

    @Test
    void testGetRandomQuotes_Success() {

        QuotesResponse quote = new QuotesResponse();
        QuotesResponse[] body = {quote};

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(QuotesResponse[].class)
        )).thenReturn(ResponseEntity.ok(body));

        QuotesResponse result = quotesService.getRandomQuotes();

        assertNotNull(result);
        assertEquals(quote, result);

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(QuotesResponse[].class)
        );
    }

    @Test
    void testGetRandomQuotes_EmptyBody() {

        QuotesResponse[] body = {};

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(QuotesResponse[].class)
        )).thenReturn(ResponseEntity.ok(body));

        QuotesResponse result = quotesService.getRandomQuotes();

        assertNull(result);
    }

    @Test
    void testGetRandomQuotes_NonSuccessStatus() {

        QuotesResponse[] body = {new QuotesResponse()};

        ResponseEntity<QuotesResponse[]> response =
                new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(QuotesResponse[].class)
        )).thenReturn(response);

        QuotesResponse result = quotesService.getRandomQuotes();

        assertNull(result);
    }

    @Test
    void testGetRandomQuotes_Exception() {

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(QuotesResponse[].class)
        )).thenThrow(new RestClientException("API Error"));

        QuotesResponse result = quotesService.getRandomQuotes();

        assertNull(result);

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(QuotesResponse[].class)
        );
    }
}