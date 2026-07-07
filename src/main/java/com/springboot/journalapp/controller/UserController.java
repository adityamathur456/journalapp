package com.springboot.journalapp.controller;

import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserEntity> getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userService.findByUserName(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserEntity user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        UserEntity updated = userService.updateUser(userName, user);

        if (updated != null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updated);
        }

        return ResponseEntity.notFound().build();
    }

}
