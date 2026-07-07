package com.springboot.journalapp.controller;

import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all-users")
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/create-admin")
    public ResponseEntity<UserEntity> createAdmin(@RequestBody UserEntity admin) {
        UserEntity savedAdmin = userService.saveNewUserAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }
}
