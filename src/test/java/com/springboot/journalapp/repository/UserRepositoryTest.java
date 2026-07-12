package com.springboot.journalapp.repository;

import com.springboot.journalapp.entity.UserEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Disabled
public class UserRepositoryTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Test
    public void testUser() {
        userRepository.getUserForSA();
    }
}
