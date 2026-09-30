package com.buildthefix.app.service;

import com.buildthefix.app.dto.PaymentIntentResponseDto;
import java.math.BigDecimal;

/**
 * Interface defining the contract for processing payments and managing checkout sessions/intents.
 */
public interface PaymentGateway {
    PaymentIntentResponseDto createPaymentIntent(BigDecimal amount, String currency, String userEmail, Long solutionId);
    boolean verifyAndConfirmPayment(String paymentIntentId);
}
