package com.buildthefix.app.service;

import com.buildthefix.app.dto.ClaimProblemRequestDto;
import com.buildthefix.app.dto.ProblemResponseDto;
import com.buildthefix.app.dto.SubmitSolutionRequestDto;
import com.buildthefix.app.entity.Developer;
import com.buildthefix.app.entity.ProblemStatement;
import com.buildthefix.app.entity.ProblemStatus;
import com.buildthefix.app.entity.Solution;
import com.buildthefix.app.exception.ResourceNotFoundException;
import com.buildthefix.app.repository.DeveloperRepository;
import com.buildthefix.app.repository.ProblemStatementRepository;
import com.buildthefix.app.repository.SolutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating developer claims and demo solution submissions.
 */
@Service
public class SolutionService {

    private final ProblemStatementRepository problemRepository;
    private final SolutionRepository solutionRepository;
    private final DeveloperRepository developerRepository;

    public SolutionService(ProblemStatementRepository problemRepository,
                           SolutionRepository solutionRepository,
                           DeveloperRepository developerRepository) {
        this.problemRepository = problemRepository;
        this.solutionRepository = solutionRepository;
        this.developerRepository = developerRepository;
    }

    @Transactional
    public ProblemResponseDto claimProblem(Long problemId, ClaimProblemRequestDto dto) {
        ProblemStatement problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem statement not found with ID: " + problemId));

        if (problem.getStatus() != ProblemStatus.OPEN) {
            throw new IllegalStateException("Cannot claim a problem that is already " + problem.getStatus());
        }

        problem.setStatus(ProblemStatus.IN_PROGRESS);

        // Link developer entity if found
        developerRepository.findAll().stream()
                .filter(d -> d.getGithubUsername() != null && d.getGithubUsername().equalsIgnoreCase(dto.getDeveloperGithub()))
                .findFirst()
                .ifPresent(problem::setClaimedDeveloper);

        ProblemStatement updated = problemRepository.save(problem);
        return new ProblemResponseDto(updated);
    }

    @Transactional
    public ProblemResponseDto submitSolution(Long problemId, SubmitSolutionRequestDto dto) {
        ProblemStatement problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem statement not found with ID: " + problemId));

        String devName = dto.getDeveloperName() != null ? dto.getDeveloperName() :
                (problem.getClaimedDeveloper() != null ? problem.getClaimedDeveloper().getName() : "Anonymous Developer");

        String devGithub = dto.getDeveloperGithub() != null ? dto.getDeveloperGithub() :
                (problem.getClaimedDeveloper() != null ? problem.getClaimedDeveloper().getGithubUsername() : "dev");

        Solution solution = new Solution(
                problem,
                devName,
                devGithub,
                dto.getDemoUrl(),
                dto.getPrice()
        );

        if (dto.getFullAccessCodeUrl() != null && !dto.getFullAccessCodeUrl().isBlank()) {
            solution.setFullAccessCodeUrl(dto.getFullAccessCodeUrl());
        } else {
            // Default generated full access repository placeholder URL if unprovided
            solution.setFullAccessCodeUrl("https://github.com/" + devGithub + "/buildthefix-solution-" + problemId);
        }

        if (problem.getClaimedDeveloper() != null) {
            solution.setDeveloper(problem.getClaimedDeveloper());
        }

        solutionRepository.save(solution);

        problem.setStatus(ProblemStatus.SOLVED);
        problem.getSolutions().add(solution);

        ProblemStatement updated = problemRepository.save(problem);
        return new ProblemResponseDto(updated);
    }
}
