package com.springboot.journalapp.controller;

import com.springboot.journalapp.dto.LoginRequest;
import com.springboot.journalapp.dto.RegisterRequest;
import com.springboot.journalapp.entity.UserEntity;
import com.springboot.journalapp.service.UserDetailServiceImpl;
import com.springboot.journalapp.service.UserService;
import com.springboot.journalapp.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserEntity> registerUser(@Valid @RequestBody RegisterRequest request) {

        UserEntity user = new UserEntity();
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setSentimentAnalysis(request.isSentimentAnalysis());

        return ResponseEntity.ok(userService.saveNewUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails =
                    userDetailService.loadUserByUsername(request.getUserName());

            String jwtToken = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(jwtToken);

        } catch (Exception e) {
            log.error("Exception occurred while creating authentication token", e);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }
    }
}
