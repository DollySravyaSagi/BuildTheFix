package com.buildthefix.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating a new problem submission.
 * Captures plain-text operational bottlenecks from problem posters.
 */
public class CreateProblemRequestDto {

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotBlank(message = "Client email is required")
    @Email(message = "Invalid email format")
    private String clientEmail;

    @NotBlank(message = "Problem title is required")
    @Size(min = 5, max = 250, message = "Title must be between 5 and 250 characters")
    private String rawTitle;

    @NotBlank(message = "Problem description is required")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String rawDescription;

    public CreateProblemRequestDto() {}

    public CreateProblemRequestDto(String clientName, String clientEmail, String rawTitle, String rawDescription) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.rawTitle = rawTitle;
        this.rawDescription = rawDescription;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getRawTitle() {
        return rawTitle;
    }

    public void setRawTitle(String rawTitle) {
        this.rawTitle = rawTitle;
    }

    public String getRawDescription() {
        return rawDescription;
    }

    public void setRawDescription(String rawDescription) {
        this.rawDescription = rawDescription;
    }
}
