package com.springboot.journalapp.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GreetingResponse {
    private WeatherResponse.Current weather;
    private QuotesResponse quote;
}
