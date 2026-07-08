package com.springboot.journalapp.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherResponse {

    @JsonProperty("current")
    private Current current;

    @Getter
    @Setter
    public static class Current {

        @JsonProperty("observation_time")
        private String observationTime;

        @JsonProperty("temperature")
        private int temperature;

        @JsonProperty("precip")
        private int precip;

        @JsonProperty("humidity")
        private int humidity;

        @JsonProperty("feelslike")
        private int feelsLike;

        @JsonProperty("uv_index")
        private int uvIndex;

        @JsonProperty("visibility")
        private int visibility;
    }
}