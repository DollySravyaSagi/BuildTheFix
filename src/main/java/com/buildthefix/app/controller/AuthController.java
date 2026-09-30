package com.buildthefix.app.controller;

import com.buildthefix.app.dto.AuthResponseDto;
import com.buildthefix.app.dto.LoginRequestDto;
import com.buildthefix.app.dto.RegisterRequestDto;
import com.buildthefix.app.dto.UserResponseDto;
import com.buildthefix.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing user registration, authentication, and profile lookup endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        AuthResponseDto response = userService.registerUser(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        AuthResponseDto response = userService.loginUser(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@RequestParam String email) {
        UserResponseDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}
