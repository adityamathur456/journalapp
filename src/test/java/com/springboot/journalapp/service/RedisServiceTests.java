package com.springboot.journalapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.journalapp.response.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTests {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
    }

    @Test
    void testGet_ShouldReturnObject_WhenKeyExists() throws Exception {

        String json = """
                {
                    "observation_time":"10:30 AM",
                    "temperature":30,
                    "precip":0,
                    "humidity":60,
                    "feelslike":34,
                    "uv_index":8,
                    "visibility":10
                }
                """;

        WeatherResponse.Current current = new WeatherResponse.Current();

        when(valueOperations.get("weather_of_mumbai")).thenReturn(json);
        when(objectMapper.readValue(json, WeatherResponse.Current.class))
                .thenReturn(current);

        WeatherResponse.Current result =
                redisService.get("weather_of_mumbai", WeatherResponse.Current.class);

        assertNotNull(result);
        assertEquals(current, result);

        verify(valueOperations).get("weather_of_mumbai");
    }

    @Test
    void testGet_ShouldReturnNull_WhenKeyDoesNotExist() {

        when(valueOperations.get("weather_of_mumbai")).thenReturn(null);

        WeatherResponse.Current result =
                redisService.get("weather_of_mumbai", WeatherResponse.Current.class);

        assertNull(result);

        verify(valueOperations).get("weather_of_mumbai");
        verifyNoInteractions(objectMapper);
    }

    @Test
    void testSet_ShouldStoreJsonInRedis() throws Exception {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        WeatherResponse.Current current = new WeatherResponse.Current();
        String json = "{\"temperature\":30}";

        when(objectMapper.writeValueAsString(current)).thenReturn(json);

        redisService.set("weather_of_mumbai", current, Duration.ofMinutes(5));

        verify(valueOperations).set(
                "weather_of_mumbai",
                json,
                Duration.ofMinutes(5)
        );
    }

    @Test
    void testSet_ShouldNotThrow_WhenSerializationFails() throws Exception {

        WeatherResponse.Current current = new WeatherResponse.Current();

        when(objectMapper.writeValueAsString(current))
                .thenThrow(new RuntimeException("Serialization failed"));

        assertDoesNotThrow(() ->
                redisService.set(
                        "weather_of_mumbai",
                        current,
                        Duration.ofMinutes(5)
                )
        );

        verify(objectMapper).writeValueAsString(current);
        verifyNoInteractions(redisTemplate);
    }
}