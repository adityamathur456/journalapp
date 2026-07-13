package com.springboot.journalapp.service;

import com.springboot.journalapp.response.QuotesResponse;
import com.springboot.journalapp.cache.AppCache;
import com.springboot.journalapp.enums.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class QuotesService {

    @Value("${quotes.api.key}")
    private String apiKey;

    private final AppCache appCache;

    private final RestTemplate restTemplate;

    public QuotesService(RestTemplate restTemplate, AppCache appCache) {
        this.restTemplate = restTemplate;
        this.appCache = appCache;
    }

    public QuotesResponse getRandomQuotes() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = appCache.getValue(Keys.QUOTES_API);

        try {
            log.info("Calling Quotes API");
            ResponseEntity<QuotesResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    QuotesResponse[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody().length > 0) {
                log.info("Quotes API responded successfully.");
                return  response.getBody()[0];
            }

        } catch (RestClientException e) {
            log.error("Error while calling Quotes API", e);
        }

        return null;
    }

}
