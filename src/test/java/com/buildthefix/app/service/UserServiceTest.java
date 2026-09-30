package com.buildthefix.app.service;

import com.buildthefix.app.dto.AuthResponseDto;
import com.buildthefix.app.dto.LoginRequestDto;
import com.buildthefix.app.dto.RegisterRequestDto;
import com.buildthefix.app.dto.UserResponseDto;
import com.buildthefix.app.entity.Developer;
import com.buildthefix.app.entity.ProblemPoster;
import com.buildthefix.app.entity.User;
import com.buildthefix.app.entity.UserRole;
import com.buildthefix.app.exception.UnauthorizedException;
import com.buildthefix.app.repository.UserRepository;
import com.buildthefix.app.util.PasswordEncoderUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private RegisterRequestDto posterRegisterDto;
    private RegisterRequestDto devRegisterDto;

    @BeforeEach
    void setUp() {
        posterRegisterDto = new RegisterRequestDto("Sarah Jenkins", "sarah@sweetdelights.com", "password123", UserRole.PROBLEM_POSTER);
        posterRegisterDto.setCompanyName("Sweet Delights Bakery");

        devRegisterDto = new RegisterRequestDto("Leo Vance", "leo@dev.com", "password123", UserRole.DEVELOPER);
        devRegisterDto.setGithubUsername("leodev");
        devRegisterDto.setPortfolioUrl("https://leodev.io");
    }

    @Test
    @DisplayName("Should register ProblemPoster subclass successfully")
    void testRegisterProblemPoster() {
        when(userRepository.existsByEmail(posterRegisterDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            ProblemPoster poster = invocation.getArgument(0);
            poster.setId(1L);
            return poster;
        });

        AuthResponseDto response = userService.registerUser(posterRegisterDto);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("Sarah Jenkins", response.getUser().getName());
        assertEquals(UserRole.PROBLEM_POSTER, response.getUser().getRole());
        assertEquals("Sweet Delights Bakery", response.getUser().getCompanyName());

        verify(userRepository, times(1)).save(any(ProblemPoster.class));
    }

    @Test
    @DisplayName("Should register Developer subclass successfully")
    void testRegisterDeveloper() {
        when(userRepository.existsByEmail(devRegisterDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            Developer dev = invocation.getArgument(0);
            dev.setId(2L);
            return dev;
        });

        AuthResponseDto response = userService.registerUser(devRegisterDto);

        assertNotNull(response);
        assertEquals("Leo Vance", response.getUser().getName());
        assertEquals(UserRole.DEVELOPER, response.getUser().getRole());
        assertEquals("leodev", response.getUser().getGithubUsername());

        verify(userRepository, times(1)).save(any(Developer.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email already exists")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail(posterRegisterDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(posterRegisterDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void testLoginSuccess() {
        String hashedPassword = PasswordEncoderUtils.hashPassword("password123");
        ProblemPoster existingUser = new ProblemPoster("Sarah Jenkins", "sarah@sweetdelights.com", hashedPassword, "Sweet Delights Bakery");
        existingUser.setId(1L);

        when(userRepository.findByEmail("sarah@sweetdelights.com")).thenReturn(Optional.of(existingUser));

        LoginRequestDto loginDto = new LoginRequestDto("sarah@sweetdelights.com", "password123");
        AuthResponseDto response = userService.loginUser(loginDto);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals("sarah@sweetdelights.com", response.getUser().getEmail());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException on invalid password")
    void testLoginInvalidPassword() {
        String hashedPassword = PasswordEncoderUtils.hashPassword("correct_password");
        ProblemPoster existingUser = new ProblemPoster("Sarah Jenkins", "sarah@sweetdelights.com", hashedPassword, "Sweet Delights Bakery");

        when(userRepository.findByEmail("sarah@sweetdelights.com")).thenReturn(Optional.of(existingUser));

        LoginRequestDto loginDto = new LoginRequestDto("sarah@sweetdelights.com", "wrong_password");
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(loginDto));
    }
}
