package com.springboot.journalapp.service;

import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
public class UserServiceTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // Test for finding User Names through user repository
    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {"aditya@456", "rocky", "admin"})
    public void testFindByUserName(String name) {
        assertNotNull(userRepository.findByUserName(name));
    }

    // Test for creating users to check saveNewUser method of UserService
    @Disabled
    @ParameterizedTest
    @ArgumentsSource(UserArgumentsProvider.class)
    public void testCreateUser(UserEntity user) {
        assertEquals(user, userService.saveNewUser(user));
    }

    // Test for deleting user to check deleteUser method of UserService
    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {"Roman", "Michael"})
    public void  testDeleteByUserName(String userName) {
        assertTrue(userService.deleteUser(userName));
    }
}
