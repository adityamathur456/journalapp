package com.springboot.journalapp.service;

import com.springboot.journalapp.response.WeatherResponse;
import com.springboot.journalapp.cache.AppCache;
import com.springboot.journalapp.enums.Keys;
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

    private final AppCache appCache;

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate, AppCache appCache) {
        this.restTemplate = restTemplate;
        this.appCache = appCache;
    }

    public WeatherResponse.Current getWeather(String city) {
        String url = UriComponentsBuilder
                .fromUriString(appCache.getValue(Keys.WEATHER_API))
                .queryParam("access_key", apiKey)
                .queryParam("query", city)
                .toUriString();
        try{
            log.info("Calling Weather API for city : {}", city);

            ResponseEntity<WeatherResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, WeatherResponse.class);
            WeatherResponse body = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && body.getCurrent() != null) {
                log.info("Weather API responded successfully.");
                return response.getBody().getCurrent();
            }
        } catch (RestClientException e) {
            log.error("Error while calling Weather API", e);
        }

        return null;
    }

}
