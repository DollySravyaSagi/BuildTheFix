package com.buildthefix.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for initiating a Stripe PaymentIntent checkout session.
 */
public class CreatePaymentIntentRequestDto {

    @NotNull(message = "Solution ID is required")
    private Long solutionId;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    private String currency = "usd";

    public CreatePaymentIntentRequestDto() {}

    public CreatePaymentIntentRequestDto(Long solutionId, String userEmail, String currency) {
        this.solutionId = solutionId;
        this.userEmail = userEmail;
        if (currency != null && !currency.isBlank()) {
            this.currency = currency;
        }
    }

    public Long getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(Long solutionId) {
        this.solutionId = solutionId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
