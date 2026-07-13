package com.springboot.journalapp.cache;

import com.springboot.journalapp.entity.ConfigJournalEntity;
import com.springboot.journalapp.enums.Keys;
import com.springboot.journalapp.repository.ConfigJournalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppCacheTest {

    @Mock
    private ConfigJournalRepository configJournalRepository;

    @InjectMocks
    private AppCache appCache;

    @Test
    void testInit_LoadCacheSuccessfully() {

        ConfigJournalEntity weather = new ConfigJournalEntity();
        weather.setKey(Keys.WEATHER_API.name());
        weather.setValue("https://weather-api.com");

        ConfigJournalEntity quotes = new ConfigJournalEntity();
        quotes.setKey(Keys.QUOTES_API.name());
        quotes.setValue("https://quotes-api.com");

        when(configJournalRepository.findAll())
                .thenReturn(List.of(weather, quotes));

        appCache.init();

        assertEquals(
                "https://weather-api.com",
                appCache.getValue(Keys.WEATHER_API));

        assertEquals(
                "https://quotes-api.com",
                appCache.getValue(Keys.QUOTES_API));

        verify(configJournalRepository).findAll();
    }

    @Test
    void testInit_WhenRepositoryReturnsEmptyList() {

        when(configJournalRepository.findAll())
                .thenReturn(List.of());

        appCache.init();

        assertNull(appCache.getValue(Keys.WEATHER_API));
        assertNull(appCache.getValue(Keys.QUOTES_API));

        verify(configJournalRepository).findAll();
    }

    @Test
    void testInit_ShouldRefreshCache() {

        ConfigJournalEntity weather = new ConfigJournalEntity();
        weather.setKey(Keys.WEATHER_API.name());
        weather.setValue("old-url");

        when(configJournalRepository.findAll())
                .thenReturn(List.of(weather));

        appCache.init();

        assertEquals("old-url",
                appCache.getValue(Keys.WEATHER_API));

        ConfigJournalEntity updated = new ConfigJournalEntity();
        updated.setKey(Keys.WEATHER_API.name());
        updated.setValue("new-url");

        when(configJournalRepository.findAll())
                .thenReturn(List.of(updated));

        appCache.init();

        assertEquals("new-url",
                appCache.getValue(Keys.WEATHER_API));
    }
}