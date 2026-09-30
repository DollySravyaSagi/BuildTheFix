package com.buildthefix.app.dto;

import java.math.BigDecimal;

/**
 * DTO returning Stripe payment intent metadata to the client.
 */
public class PaymentIntentResponseDto {
    private String paymentIntentId;
    private String clientSecret;
    private BigDecimal amount;
    private String currency;
    private String status;

    public PaymentIntentResponseDto() {}

    public PaymentIntentResponseDto(String paymentIntentId, String clientSecret, BigDecimal amount, String currency, String status) {
        this.paymentIntentId = paymentIntentId;
        this.clientSecret = clientSecret;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
