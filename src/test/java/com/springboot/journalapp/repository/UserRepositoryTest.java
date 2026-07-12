package com.springboot.journalapp.repository;

import com.springboot.journalapp.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByUserName() {

        UserEntity user = UserEntity.builder()
                .userName("aditya")
                .password("password")
                .roles(List.of("USER"))
                .build();

        userRepository.save(user);

        UserEntity found = userRepository.findByUserName("aditya");

        assertNotNull(found);
        assertEquals("aditya", found.getUserName());
        assertEquals("password", found.getPassword());
    }

    @Test
    void testDeleteByUserName() {

        UserEntity user = UserEntity.builder()
                .userName("roman")
                .password("password")
                .roles(List.of("USER"))
                .build();

        userRepository.save(user);

        long deleted = userRepository.deleteByUserName("roman");

        assertEquals(1, deleted);
        assertNull(userRepository.findByUserName("roman"));
    }

    @Test
    void testFindByUserName_UserNotFound() {

        UserEntity user = userRepository.findByUserName("unknown");

        assertNull(user);
    }

    @Test
    void testDeleteByUserName_UserNotFound() {

        long deleted = userRepository.deleteByUserName("unknown");

        assertEquals(0, deleted);
    }
}