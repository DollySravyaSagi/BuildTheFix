package com.buildthefix.app.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for claiming an open problem statement.
 */
public class ClaimProblemRequestDto {

    @NotBlank(message = "Developer name is required")
    private String developerName;

    @NotBlank(message = "Developer GitHub username is required")
    private String developerGithub;

    public ClaimProblemRequestDto() {}

    public ClaimProblemRequestDto(String developerName, String developerGithub) {
        this.developerName = developerName;
        this.developerGithub = developerGithub;
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
}
