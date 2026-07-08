package com.springboot.journalapp.service;

import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserDetailServiceImplTests {

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Mock
    private UserRepository userRepository;

    @Test
    @Disabled
    void loadUserByUsernameTest() {
        when(userRepository.findByUserName("aditya@456")).thenReturn(
                UserEntity.builder()
                        .userName("aditya@456")
                        .password("aditya4838")
                        .roles(new ArrayList<>())
                        .build()
        );

        UserDetails user = userDetailService.loadUserByUsername("aditya@456");
        System.out.println(user.getUsername() +" "+ user.getPassword());
        assertNotNull(user);
    }
}
