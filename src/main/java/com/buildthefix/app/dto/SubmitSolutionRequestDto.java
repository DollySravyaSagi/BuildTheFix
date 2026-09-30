package com.buildthefix.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Data Transfer Object for developers submitting a working demo solution.
 */
public class SubmitSolutionRequestDto {

    private String developerName;
    private String developerGithub;

    @NotBlank(message = "Solution deployment demo URL is required")
    private String demoUrl;

    private String fullAccessCodeUrl;

    @NotNull(message = "Solution access price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price = new BigDecimal("49.00");

    public SubmitSolutionRequestDto() {}

    public SubmitSolutionRequestDto(String developerName, String developerGithub, String demoUrl, String fullAccessCodeUrl, BigDecimal price) {
        this.developerName = developerName;
        this.developerGithub = developerGithub;
        this.demoUrl = demoUrl;
        this.fullAccessCodeUrl = fullAccessCodeUrl;
        if (price != null) {
            this.price = price;
        }
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
}
