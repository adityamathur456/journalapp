package com.springboot.journalapp.service;

import com.springboot.journalapp.api.QuotesResponse;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Service
public class QuotesService {
    private static final String API_KEY = "oRSlp0nWsqYjQXUX4i9htAMdYCsaZo9olUOY1SzW";

    private static final String URL = "https://api.api-ninjas.com/v2/randomquotes?categories=success,wisdom";

    private final RestTemplate restTemplate;

    public QuotesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public QuotesResponse getRandomQuotes() {
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("X-Api-Key", API_KEY);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<QuotesResponse[]> response = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                entity,
                QuotesResponse[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody().length > 0) {
            return  response.getBody()[0];
        }

        return null;
    }
}
