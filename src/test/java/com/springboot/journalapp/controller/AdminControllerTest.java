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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {

        UserEntity user1 = UserEntity.builder()
                .userName("aditya")
                .password("password")
                .build();

        UserEntity user2 = UserEntity.builder()
                .userName("john")
                .password("password")
                .build();

        when(userService.getAllUsers())
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/admin/all-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userName").value("aditya"))
                .andExpect(jsonPath("$[1].userName").value("john"));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateAdmin() throws Exception {

        UserEntity admin = UserEntity.builder()
                .userName("admin")
                .password("password")
                .build();

        when(userService.saveNewUserAdmin(any(UserEntity.class)))
                .thenReturn(admin);

        mockMvc.perform(post("/admin/create-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("admin"));

        verify(userService).saveNewUserAdmin(any(UserEntity.class));
    }
}