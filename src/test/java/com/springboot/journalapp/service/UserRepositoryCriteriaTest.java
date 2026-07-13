package com.springboot.journalapp.service;

import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryCriteriaTest {

    @Autowired
    private UserRepositoryCriteria userRepositoryImpl;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        userRepository.deleteByUserName("criteriaUser");
    }

    @Test
    void getUserForSATest() {

        UserEntity user = new UserEntity();
        user.setUserName("criteriaUser");
        user.setPassword("password");
        user.setEmail("criteria@test.com");
        user.setSentimentAnalysis(true);

        userRepository.save(user);

        List<UserEntity> users =
                userRepositoryImpl.getUserForSA();

        assertNotNull(users);

        assertTrue(
                users.stream()
                        .anyMatch(u ->
                                u.getUserName().equals("criteriaUser"))
        );
    }
}