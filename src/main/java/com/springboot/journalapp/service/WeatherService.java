package com.springboot.journalapp.service;

import com.springboot.journalapp.api.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;
    private static final String URL = "http://api.weatherstack.com/current?access_key=%s&query=%s";

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherResponse.Current getWeather(String city) {
        String url = UriComponentsBuilder
                .fromUriString(URL)
                .queryParam("access_key", apiKey)
                .queryParam("query", city)
                .toUriString();
        try{
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, WeatherResponse.class);
            return response.getStatusCode().is2xxSuccessful() ? response.getBody().getCurrent() : null;
        } catch (RestClientException e) {
            log.error("Request Failed Response {}",e.getMessage());
            return null;
        }
    }
}
