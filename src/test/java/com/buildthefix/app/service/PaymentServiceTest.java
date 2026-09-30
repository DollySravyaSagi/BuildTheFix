package com.buildthefix.app.service;

import com.buildthefix.app.dto.CreatePaymentIntentRequestDto;
import com.buildthefix.app.dto.PaymentIntentResponseDto;
import com.buildthefix.app.dto.ProblemResponseDto;
import com.buildthefix.app.dto.UnlockSolutionRequestDto;
import com.buildthefix.app.entity.Payment;
import com.buildthefix.app.entity.ProblemStatement;
import com.buildthefix.app.entity.Solution;
import com.buildthefix.app.repository.PaymentRepository;
import com.buildthefix.app.repository.ProblemStatementRepository;
import com.buildthefix.app.repository.SolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private ProblemStatementRepository problemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PaymentService paymentService;

    private Solution testSolution;
    private ProblemStatement testProblem;

    @BeforeEach
    void setUp() {
        testProblem = new ProblemStatement("Client", "client@test.com", "Title", "Description");
        testProblem.setId(1L);

        testSolution = new Solution(testProblem, "Leo Dev", "leodev", "https://demo.app", new BigDecimal("49.00"));
        testSolution.setId(5L);
        testSolution.setFullAccessCodeUrl("https://github.com/leodev/full-code");

        testProblem.getSolutions().add(testSolution);
    }

    @Test
    @DisplayName("Should create Stripe PaymentIntent matching developer solution price")
    void testCreatePaymentIntent() {
        when(solutionRepository.findById(5L)).thenReturn(Optional.of(testSolution));

        PaymentIntentResponseDto mockGatewayRes = new PaymentIntentResponseDto(
                "pi_test_123",
                "pi_test_123_secret",
                new BigDecimal("49.00"),
                "usd",
                "requires_payment_method"
        );

        when(paymentGateway.createPaymentIntent(eq(new BigDecimal("49.00")), eq("usd"), eq("buyer@test.com"), eq(5L)))
                .thenReturn(mockGatewayRes);

        CreatePaymentIntentRequestDto requestDto = new CreatePaymentIntentRequestDto(5L, "buyer@test.com", "usd");
        PaymentIntentResponseDto response = paymentService.createPaymentIntent(requestDto);

        assertNotNull(response);
        assertEquals("pi_test_123", response.getPaymentIntentId());
        assertEquals(new BigDecimal("49.00"), response.getAmount());

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should unlock solution and reveal full access source link")
    void testUnlockSolution() {
        when(problemRepository.findById(1L)).thenReturn(Optional.of(testProblem));
        when(paymentGateway.verifyAndConfirmPayment("pi_test_123")).thenReturn(true);
        when(problemRepository.save(any(ProblemStatement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UnlockSolutionRequestDto unlockDto = new UnlockSolutionRequestDto("buyer@test.com", "pi_test_123");
        ProblemResponseDto response = paymentService.unlockSolution(1L, unlockDto);

        assertNotNull(response);
        assertTrue(response.getSolutions().get(0).isUnlocked());
        assertEquals("https://github.com/leodev/full-code", response.getSolutions().get(0).getFullAccessCodeUrl());

        verify(solutionRepository, times(1)).save(any(Solution.class));
    }
}
