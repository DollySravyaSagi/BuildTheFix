package com.buildthefix.app.controller;

import com.buildthefix.app.dto.CreatePaymentIntentRequestDto;
import com.buildthefix.app.dto.PaymentIntentResponseDto;
import com.buildthefix.app.dto.ProblemResponseDto;
import com.buildthefix.app.dto.UnlockSolutionRequestDto;
import com.buildthefix.app.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for processing Stripe payments and unlocking full solution repository access.
 */
@RestController
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/payments/create-intent")
    public ResponseEntity<PaymentIntentResponseDto> createPaymentIntent(@Valid @RequestBody CreatePaymentIntentRequestDto requestDto) {
        PaymentIntentResponseDto response = paymentService.createPaymentIntent(requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/problems/{id}/unlock")
    public ResponseEntity<ProblemResponseDto> unlockSolution(
            @PathVariable Long id,
            @RequestBody(required = false) UnlockSolutionRequestDto requestDto) {

        if (requestDto == null) {
            requestDto = new UnlockSolutionRequestDto();
        }

        ProblemResponseDto response = paymentService.unlockSolution(id, requestDto);
        return ResponseEntity.ok(response);
    }
}
