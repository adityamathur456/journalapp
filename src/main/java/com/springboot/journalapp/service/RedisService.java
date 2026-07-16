package com.springboot.journalapp.service;

import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, String> redisTemplate,
                        ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object value = redisTemplate.opsForValue().get(key);

            if (value == null) return null;

            return objectMapper.readValue(value.toString(), entityClass);
        } catch (Exception e) {
            log.error("Error reading Redis key: {}", key, e);
            return null;
        }
    }

    public void set(String key, Object value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (Exception e) {
            log.error("Error writing Redis key: {}", key, e);
        }
    }
}