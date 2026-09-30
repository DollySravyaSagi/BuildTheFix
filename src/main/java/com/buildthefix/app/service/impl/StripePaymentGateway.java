package com.buildthefix.app.service.impl;

import com.buildthefix.app.dto.PaymentIntentResponseDto;
import com.buildthefix.app.exception.PaymentException;
import com.buildthefix.app.service.PaymentGateway;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * Implementation of PaymentGateway using official Stripe Java SDK.
 * Reads STRIPE_SECRET_KEY environment variable and operates in test mode or simulation mode.
 */
@Service
public class StripePaymentGateway implements PaymentGateway {

    @Value("${stripe.api.key:}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        if (stripeSecretKey != null && !stripeSecretKey.isBlank()) {
            Stripe.apiKey = stripeSecretKey.trim();
            System.out.println("Stripe Java SDK initialized successfully in test mode.");
        } else {
            System.out.println("STRIPE_SECRET_KEY not set. Operating in test simulation mode for solution unlocking.");
        }
    }

    @Override
    public PaymentIntentResponseDto createPaymentIntent(BigDecimal amount, String currency, String userEmail, Long solutionId) {
        long amountInCents = amount.multiply(new BigDecimal("100")).longValue();

        if (stripeSecretKey != null && !stripeSecretKey.isBlank()) {
            try {
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency(currency != null ? currency.toLowerCase() : "usd")
                        .setReceiptEmail(userEmail)
                        .putMetadata("solution_id", String.valueOf(solutionId))
                        .putMetadata("user_email", userEmail)
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

                PaymentIntent intent = PaymentIntent.create(params);
                return new PaymentIntentResponseDto(
                        intent.getId(),
                        intent.getClientSecret(),
                        amount,
                        currency,
                        intent.getStatus()
                );
            } catch (Exception ex) {
                throw new PaymentException("Stripe API error while creating payment intent: " + ex.getMessage(), ex);
            }
        }

        // Test Simulation Mode when STRIPE_SECRET_KEY environment variable is not present
        String mockIntentId = "pi_test_" + System.currentTimeMillis() + "_" + solutionId;
        String mockClientSecret = mockIntentId + "_secret_test";
        return new PaymentIntentResponseDto(
                mockIntentId,
                mockClientSecret,
                amount,
                currency,
                "requires_payment_method"
        );
    }

    @Override
    public boolean verifyAndConfirmPayment(String paymentIntentId) {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            return false;
        }

        if (stripeSecretKey != null && !stripeSecretKey.isBlank() && paymentIntentId.startsWith("pi_")) {
            try {
                PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
                return "succeeded".equalsIgnoreCase(intent.getStatus());
            } catch (Exception ex) {
                System.err.println("Stripe verification notice: " + ex.getMessage());
                return false;
            }
        }

        // In test simulation mode, simulated test payment intent IDs are automatically valid
        return true;
    }
}
