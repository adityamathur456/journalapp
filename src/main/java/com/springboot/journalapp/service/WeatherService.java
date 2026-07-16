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

import java.time.Duration;

@Slf4j
@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final AppCache appCache;

    private final RestTemplate restTemplate;

    private final RedisService redisService;

    public WeatherService(RestTemplate restTemplate, AppCache appCache, RedisService redisService) {
        this.restTemplate = restTemplate;
        this.appCache = appCache;
        this.redisService = redisService;
    }

    public WeatherResponse.Current getWeather(String city) {
        String key = "weather_of_" + city.trim().toLowerCase().replaceAll("\\s+", "");
        WeatherResponse.Current weatherResponse = redisService.get(key, WeatherResponse.Current.class);

        if (weatherResponse != null) {
            log.info("Weather fetched from Redis for city: {}", city);
            return weatherResponse;
        }

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
                redisService.set(key, body.getCurrent(), Duration.ofMinutes(5));
                log.info("Weather API responded successfully.");
                return body.getCurrent();
            }
        } catch (RestClientException e) {
            log.error("Error while calling Weather API", e);
        }

        return null;
    }

}
