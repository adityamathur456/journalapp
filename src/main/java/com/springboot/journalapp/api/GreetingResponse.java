package com.springboot.journalapp.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GreetingResponse {
    private WeatherResponse.Current weather;
    private QuotesResponse quote;
}
