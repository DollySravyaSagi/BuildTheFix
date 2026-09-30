package com.buildthefix.app.dto;

/**
 * Data Transfer Object for unlocking a solution after Stripe payment.
 */
public class UnlockSolutionRequestDto {

    private String userEmail;
    private String paymentIntentId;

    public UnlockSolutionRequestDto() {}

    public UnlockSolutionRequestDto(String userEmail, String paymentIntentId) {
        this.userEmail = userEmail;
        this.paymentIntentId = paymentIntentId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }
}
