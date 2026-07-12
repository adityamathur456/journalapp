package com.springboot.journalapp.service;

import com.springboot.journalapp.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTests {

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void cleanBefore() {
        userService.deleteUser("Michael");
        userService.deleteUser("Roman");
        userService.deleteUser("TestUser");
    }

    @AfterEach
    void cleanAfter() {
        userService.deleteUser("Michael");
        userService.deleteUser("Roman");
        userService.deleteUser("TestUser");
    }

    @ParameterizedTest
    @ArgumentsSource(UserArgumentsProvider.class)
    void testSaveNewUser(UserEntity user) {

        String rawPassword = user.getPassword();   // Save before encoding

        UserEntity saved = userService.saveNewUser(user);

        assertNotNull(saved);
        assertNotNull(saved.getId());

        assertEquals(user.getUserName(), saved.getUserName());

        assertTrue(
                encoder.matches(rawPassword, saved.getPassword())
        );

        assertEquals(List.of("USER"), saved.getRoles());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Michael", "Roman"})
    void testFindByUserName(String username) {

        UserEntity user = new UserEntity();
        user.setUserName(username);
        user.setPassword("password");

        userService.saveNewUser(user);

        UserEntity found = userService.findByUserName(username);

        assertNotNull(found);
        assertEquals(username, found.getUserName());
    }

    @Test
    void testGetAllUsers() {

        UserEntity user = new UserEntity();
        user.setUserName("TestUser");
        user.setPassword("password");

        userService.saveNewUser(user);

        List<UserEntity> users = userService.getAllUsers();

        assertFalse(users.isEmpty());

        assertTrue(
                users.stream()
                        .anyMatch(u -> u.getUserName().equals("TestUser"))
        );
    }

    @Test
    void testFindUserById() {

        UserEntity user = new UserEntity();
        user.setUserName("TestUser");
        user.setPassword("password");

        UserEntity saved = userService.saveNewUser(user);

        assertNotNull(saved);

        assertTrue(
                userService.findUserById(saved.getId()).isPresent()
        );
    }

    @Test
    void testUpdateUser() {

        UserEntity user = new UserEntity();
        user.setUserName("TestUser");
        user.setPassword("password");

        userService.saveNewUser(user);

        UserEntity updated = new UserEntity();
        updated.setUserName("UpdatedUser");
        updated.setPassword("newPassword");

        userService.updateUser("TestUser", updated);

        UserEntity result = userService.findByUserName("UpdatedUser");

        assertNotNull(result);
        assertEquals("UpdatedUser", result.getUserName());
        assertTrue(encoder.matches("newPassword", result.getPassword()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Michael", "Roman"})
    void testDeleteUser(String username) {

        UserEntity user = new UserEntity();
        user.setUserName(username);
        user.setPassword("password");

        userService.saveNewUser(user);

        assertTrue(userService.deleteUser(username));

        assertNull(userService.findByUserName(username));
    }

    @Test
    void testSaveNewAdmin() {

        UserEntity admin = new UserEntity();
        admin.setUserName("Admin");
        admin.setPassword("admin123");

        UserEntity saved =
                userService.saveNewUserAdmin(admin);

        assertNotNull(saved);

        assertEquals(
                List.of("USER", "ADMIN"),
                saved.getRoles()
        );

        userService.deleteUser("Admin");
    }
}