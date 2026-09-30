package com.buildthefix.app.controller;

import com.buildthefix.app.dto.ClaimProblemRequestDto;
import com.buildthefix.app.dto.ProblemResponseDto;
import com.buildthefix.app.dto.SubmitSolutionRequestDto;
import com.buildthefix.app.service.SolutionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing developer action endpoints for claiming problems and submitting demo links.
 */
@RestController
@RequestMapping("/api/problems")
@CrossOrigin(origins = "*")
public class SolutionController {

    private final SolutionService solutionService;

    public SolutionController(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    @PatchMapping("/{id}/claim")
    public ResponseEntity<ProblemResponseDto> claimProblem(
            @PathVariable Long id,
            @Valid @RequestBody ClaimProblemRequestDto requestDto) {
        ProblemResponseDto response = solutionService.claimProblem(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/solve")
    public ResponseEntity<ProblemResponseDto> solveProblem(
            @PathVariable Long id,
            @Valid @RequestBody SubmitSolutionRequestDto requestDto) {
        ProblemResponseDto response = solutionService.submitSolution(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/solutions")
    public ResponseEntity<ProblemResponseDto> addSolution(
            @PathVariable Long id,
            @Valid @RequestBody SubmitSolutionRequestDto requestDto) {
        ProblemResponseDto response = solutionService.submitSolution(id, requestDto);
        return ResponseEntity.ok(response);
    }
}
