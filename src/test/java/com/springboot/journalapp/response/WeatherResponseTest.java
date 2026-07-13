package com.springboot.journalapp.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherResponseTest {

    @Test
    void testWeatherResponse() {

        WeatherResponse.Current current = new WeatherResponse.Current();

        current.setObservationTime("10:00 AM");
        current.setTemperature(28);
        current.setPrecip(5);
        current.setHumidity(75);
        current.setFeelsLike(31);
        current.setUvIndex(6);
        current.setVisibility(10);

        WeatherResponse response = new WeatherResponse();
        response.setCurrent(current);

        assertNotNull(response.getCurrent());

        assertEquals("10:00 AM", response.getCurrent().getObservationTime());
        assertEquals(28, response.getCurrent().getTemperature());
        assertEquals(5, response.getCurrent().getPrecip());
        assertEquals(75, response.getCurrent().getHumidity());
        assertEquals(31, response.getCurrent().getFeelsLike());
        assertEquals(6, response.getCurrent().getUvIndex());
        assertEquals(10, response.getCurrent().getVisibility());
    }
}