package com.springboot.journalapp.controller;

import com.springboot.journalapp.response.GreetingResponse;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.QuotesService;
import com.springboot.journalapp.service.UserService;
import com.springboot.journalapp.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final WeatherService weatherService;

    private final QuotesService quotesService;

    public UserController(UserService userService, WeatherService weatherService, QuotesService quotesService) {
        this.userService = userService;
        this.weatherService = weatherService;
        this.quotesService = quotesService;
    }

    @GetMapping
    public ResponseEntity<GreetingResponse> greetingUser() {

        GreetingResponse response = new GreetingResponse();

        response.setWeather(weatherService.getWeather("Bengaluru"));
        response.setQuote(quotesService.getRandomQuotes());

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserEntity user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        UserEntity updated = userService.updateUser(userName, user);

        if (updated != null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updated);
        }

        return ResponseEntity.notFound().build();
    }

}
