package com.buildthefix.app.service;

import com.buildthefix.app.dto.CreatePaymentIntentRequestDto;
import com.buildthefix.app.dto.PaymentIntentResponseDto;
import com.buildthefix.app.dto.ProblemResponseDto;
import com.buildthefix.app.dto.UnlockSolutionRequestDto;
import com.buildthefix.app.entity.Payment;
import com.buildthefix.app.entity.PaymentStatus;
import com.buildthefix.app.entity.ProblemStatement;
import com.buildthefix.app.entity.Solution;
import com.buildthefix.app.exception.PaymentException;
import com.buildthefix.app.exception.ResourceNotFoundException;
import com.buildthefix.app.repository.PaymentRepository;
import com.buildthefix.app.repository.ProblemStatementRepository;
import com.buildthefix.app.repository.SolutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing Stripe PaymentIntent creation, payment verification, and solution unlocking.
 */
@Service
public class PaymentService {

    private final SolutionRepository solutionRepository;
    private final ProblemStatementRepository problemRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public PaymentService(SolutionRepository solutionRepository,
                          ProblemStatementRepository problemRepository,
                          PaymentRepository paymentRepository,
                          PaymentGateway paymentGateway) {
        this.solutionRepository = solutionRepository;
        this.problemRepository = problemRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public PaymentIntentResponseDto createPaymentIntent(CreatePaymentIntentRequestDto dto) {
        Solution solution = solutionRepository.findById(dto.getSolutionId())
                .orElseThrow(() -> new ResourceNotFoundException("Solution not found with ID: " + dto.getSolutionId()));

        // Create Stripe PaymentIntent matching the amount developer set
        PaymentIntentResponseDto response = paymentGateway.createPaymentIntent(
                solution.getPrice(),
                dto.getCurrency(),
                dto.getUserEmail(),
                solution.getId()
        );

        // Record transaction details in database
        Payment payment = new Payment(
                dto.getUserEmail(),
                solution,
                response.getPaymentIntentId(),
                response.getClientSecret(),
                solution.getPrice(),
                dto.getCurrency()
        );
        paymentRepository.save(payment);

        return response;
    }

    @Transactional
    public ProblemResponseDto unlockSolution(Long problemId, UnlockSolutionRequestDto dto) {
        ProblemStatement problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem statement not found with ID: " + problemId));

        List<Solution> solutions = problem.getSolutions();
        if (solutions.isEmpty()) {
            throw new ResourceNotFoundException("No solution available to unlock for problem ID: " + problemId);
        }

        // Verify Stripe PaymentIntent if provided
        if (dto.getPaymentIntentId() != null && !dto.getPaymentIntentId().isBlank()) {
            boolean isValid = paymentGateway.verifyAndConfirmPayment(dto.getPaymentIntentId());
            if (!isValid) {
                throw new PaymentException("Stripe payment verification failed for PaymentIntent ID: " + dto.getPaymentIntentId());
            }

            // Update payment record status
            paymentRepository.findByStripePaymentIntentId(dto.getPaymentIntentId())
                    .ifPresent(p -> {
                        p.setStatus(PaymentStatus.SUCCEEDED);
                        paymentRepository.save(p);
                    });
        }

        // Unlock solution and log buyer email
        for (Solution sol : solutions) {
            sol.setUnlocked(true);
            if (dto.getUserEmail() != null && !dto.getUserEmail().isBlank()) {
                if (!sol.getUnlockedByEmails().contains(dto.getUserEmail())) {
                    sol.getUnlockedByEmails().add(dto.getUserEmail());
                }
            }
            solutionRepository.save(sol);
        }

        ProblemStatement updated = problemRepository.save(problem);
        return new ProblemResponseDto(updated);
    }
}
