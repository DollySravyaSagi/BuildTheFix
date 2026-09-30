package com.buildthefix.app.dto;

/**
 * Data Transfer Object returned upon successful authentication.
 */
public class AuthResponseDto {

    private String message;
    private String token;
    private UserResponseDto user;

    public AuthResponseDto() {}

    public AuthResponseDto(String message, String token, UserResponseDto user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }
}
