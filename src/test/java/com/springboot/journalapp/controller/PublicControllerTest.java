package com.springboot.journalapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.journalapp.config.SecurityConfig;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicController.class)
@Import(SecurityConfig.class)
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHealthCheck() throws Exception {

        mockMvc.perform(get("/public/health-check"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void testCreateUser() throws Exception {

        UserEntity user = UserEntity.builder()
                .userName("aditya")
                .password("password")
                .build();

        when(userService.saveNewUser(any(UserEntity.class)))
                .thenReturn(user);

        mockMvc.perform(post("/public/create-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("aditya"));

        verify(userService).saveNewUser(any(UserEntity.class));
    }
}