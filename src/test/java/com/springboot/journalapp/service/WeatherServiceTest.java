package com.springboot.journalapp.service;

import com.springboot.journalapp.response.WeatherResponse;
import com.springboot.journalapp.cache.AppCache;
import com.springboot.journalapp.enums.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppCache appCache;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "dummy-api-key");

        when(appCache.getValue(Keys.WEATHER_API))
                .thenReturn("https://api.weatherstack.com/current");
    }

    @Test
    void testGetWeather_Success() {

        WeatherResponse.Current current = new WeatherResponse.Current();

        WeatherResponse response = new WeatherResponse();
        response.setCurrent(current);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(WeatherResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        WeatherResponse.Current result = weatherService.getWeather("Indore");

        assertNotNull(result);
        assertEquals(current, result);

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(WeatherResponse.class));
    }

    @Test
    void testGetWeather_CurrentIsNull() {

        WeatherResponse response = new WeatherResponse();
        response.setCurrent(null);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(WeatherResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        WeatherResponse.Current result = weatherService.getWeather("Indore");

        assertNull(result);
    }

    @Test
    void testGetWeather_NonSuccessStatus() {

        WeatherResponse response = new WeatherResponse();

        ResponseEntity<WeatherResponse> entity =
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(WeatherResponse.class)))
                .thenReturn(entity);

        WeatherResponse.Current result = weatherService.getWeather("Indore");

        assertNull(result);
    }

    @Test
    void testGetWeather_Exception() {

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(WeatherResponse.class)))
                .thenThrow(new RestClientException("API Error"));

        WeatherResponse.Current result = weatherService.getWeather("Indore");

        assertNull(result);

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(WeatherResponse.class));
    }
}