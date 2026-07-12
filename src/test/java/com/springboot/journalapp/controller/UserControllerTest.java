package com.springboot.journalapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.journalapp.api.QuotesResponse;
import com.springboot.journalapp.api.WeatherResponse;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.QuotesService;
import com.springboot.journalapp.service.UserService;
import com.springboot.journalapp.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private WeatherService weatherService;

    @MockitoBean
    private QuotesService quotesService;

    @Test
    @WithMockUser(username = "aditya")
    void updateUser_ShouldReturn200() throws Exception {

        UserEntity request = new UserEntity();
        request.setUserName("UpdatedUser");
        request.setPassword("password");

        UserEntity updated = new UserEntity();
        updated.setUserName("UpdatedUser");
        updated.setPassword("encodedPassword");

        when(userService.updateUser(eq("aditya"), any(UserEntity.class)))
                .thenReturn(updated);

        mockMvc.perform(
                        put("/user")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "aditya")
    void updateUser_ShouldReturn404() throws Exception {

        UserEntity request = new UserEntity();
        request.setUserName("UpdatedUser");
        request.setPassword("password");

        when(userService.updateUser(eq("aditya"), any(UserEntity.class)))
                .thenReturn(null);

        mockMvc.perform(
                        put("/user")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}