package com.buildthefix.app.service;

import com.buildthefix.app.dto.AuthResponseDto;
import com.buildthefix.app.dto.LoginRequestDto;
import com.buildthefix.app.dto.RegisterRequestDto;
import com.buildthefix.app.dto.UserResponseDto;
import com.buildthefix.app.entity.Developer;
import com.buildthefix.app.entity.ProblemPoster;
import com.buildthefix.app.entity.User;
import com.buildthefix.app.entity.UserRole;
import com.buildthefix.app.exception.ResourceNotFoundException;
import com.buildthefix.app.exception.UnauthorizedException;
import com.buildthefix.app.repository.UserRepository;
import com.buildthefix.app.util.PasswordEncoderUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating user registration, authentication, and user subclass entity instantiation.
 * Demonstrates Object-Oriented Polymorphism by constructing appropriate ProblemPoster or Developer instances based on request role.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponseDto registerUser(RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("An account with email '" + dto.getEmail() + "' already exists.");
        }

        String hashedPassword = PasswordEncoderUtils.hashPassword(dto.getPassword());
        User newUser;

        // Polymorphic Entity Creation based on requested role
        if (dto.getRole() == UserRole.PROBLEM_POSTER) {
            newUser = new ProblemPoster(
                    dto.getName(),
                    dto.getEmail(),
                    hashedPassword,
                    dto.getCompanyName()
            );
        } else if (dto.getRole() == UserRole.DEVELOPER) {
            newUser = new Developer(
                    dto.getName(),
                    dto.getEmail(),
                    hashedPassword,
                    dto.getGithubUsername(),
                    dto.getPortfolioUrl()
            );
        } else {
            throw new IllegalArgumentException("Unsupported user role: " + dto.getRole());
        }

        User savedUser = userRepository.save(newUser);
        String token = generateAuthToken(savedUser);

        return new AuthResponseDto(
                "User registered successfully",
                token,
                new UserResponseDto(savedUser)
        );
    }

    @Transactional(readOnly = true)
    public AuthResponseDto loginUser(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!PasswordEncoderUtils.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        String token = generateAuthToken(user);
        return new AuthResponseDto(
                "Login successful",
                token,
                new UserResponseDto(user)
        );
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return new UserResponseDto(user);
    }

    private String generateAuthToken(User user) {
        return "btf_token_" + user.getId() + "_" + System.currentTimeMillis();
    }
}
