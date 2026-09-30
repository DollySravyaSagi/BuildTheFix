package com.buildthefix.app.dto;

import com.buildthefix.app.entity.Solution;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing solution details sent to frontend clients.
 */
public class SolutionResponseDto {

    private Long id;
    private Long problemId;
    private String developerName;
    private String developerGithub;
    private String demoUrl;
    private String fullAccessCodeUrl;
    private BigDecimal price;
    private boolean isUnlocked;
    private LocalDateTime createdAt;

    public SolutionResponseDto() {}

    public SolutionResponseDto(Solution solution) {
        this.id = solution.getId();
        if (solution.getProblemStatement() != null) {
            this.problemId = solution.getProblemStatement().getId();
        }
        this.developerName = solution.getDeveloperName();
        this.developerGithub = solution.getDeveloperGithub();
        this.demoUrl = solution.getDemoUrl();
        this.price = solution.getPrice();
        this.isUnlocked = solution.isUnlocked();
        this.createdAt = solution.getCreatedAt();

        // Security: full access repository link is only included if solution is unlocked
        if (solution.isUnlocked()) {
            this.fullAccessCodeUrl = solution.getFullAccessCodeUrl();
        } else {
            this.fullAccessCodeUrl = null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDeveloperGithub() {
        return developerGithub;
    }

    public void setDeveloperGithub(String developerGithub) {
        this.developerGithub = developerGithub;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public String getFullAccessCodeUrl() {
        return fullAccessCodeUrl;
    }

    public void setFullAccessCodeUrl(String fullAccessCodeUrl) {
        this.fullAccessCodeUrl = fullAccessCodeUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
