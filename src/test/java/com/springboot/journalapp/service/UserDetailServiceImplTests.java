package com.springboot.journalapp.service;

import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTests {

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {

        UserEntity user = UserEntity.builder()
                .userName("aditya@456")
                .password("encodedPassword")
                .roles(List.of("USER"))
                .build();

        when(userRepository.findByUserName("aditya@456"))
                .thenReturn(user);

        UserDetails userDetails =
                userDetailService.loadUserByUsername("aditya@456");

        assertNotNull(userDetails);
        assertEquals("aditya@456", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        );

        verify(userRepository).findByUserName("aditya@456");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {

        when(userRepository.findByUserName("unknown"))
                .thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername("unknown")
        );

        verify(userRepository).findByUserName("unknown");
    }
}