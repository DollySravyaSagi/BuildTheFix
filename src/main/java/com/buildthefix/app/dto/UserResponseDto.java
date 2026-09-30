package com.buildthefix.app.dto;

import com.buildthefix.app.entity.Developer;
import com.buildthefix.app.entity.ProblemPoster;
import com.buildthefix.app.entity.User;
import com.buildthefix.app.entity.UserRole;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing public user profile information.
 */
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String companyName;
    private String githubUsername;
    private String portfolioUrl;
    private LocalDateTime createdAt;

    public UserResponseDto() {}

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();

        if (user instanceof ProblemPoster poster) {
            this.companyName = poster.getCompanyName();
        } else if (user instanceof Developer dev) {
            this.githubUsername = dev.getGithubUsername();
            this.portfolioUrl = dev.getPortfolioUrl();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
