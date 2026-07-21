package com.springboot.journalapp.controller;

import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.UserDetailServiceImpl;
import com.springboot.journalapp.service.UserService;
import com.springboot.journalapp.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserDetailServiceImpl userDetailService;
    private final JwtUtil jwtUtil;

    public PublicController(UserService userService,
                            AuthenticationManager authenticationManager,
                            UserDetailServiceImpl userDetailService,
                            JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailService = userDetailService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> registerUser(@RequestBody UserEntity user) {
        return ResponseEntity.ok(userService.saveNewUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserEntity user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
            );
            UserDetails userDetails = userDetailService.loadUserByUsername(user.getUserName());
            String jwtToken = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.ok(jwtToken);
        } catch (Exception e) {
            log.error("Exception occured while createAuthenticationToken ", e);
            return ResponseEntity.ok("Incorrect Username and password");
        }
    }
}
