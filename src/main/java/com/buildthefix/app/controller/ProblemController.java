package com.buildthefix.app.controller;

import com.buildthefix.app.dto.*;
import com.buildthefix.app.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing endpoints for problem posting, querying, platform stats, and seeding.
 */
@RestController
@CrossOrigin(origins = "*")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping("/api/problems")
    public ResponseEntity<ProblemResponseDto> createProblem(@Valid @RequestBody CreateProblemRequestDto requestDto) {
        ProblemResponseDto response = problemService.createProblem(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/problems")
    public ResponseEntity<List<ProblemResponseDto>> getAllProblems(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        List<ProblemResponseDto> problems = problemService.getAllProblems(status, search);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/api/problems/{id}")
    public ResponseEntity<ProblemResponseDto> getProblemById(@PathVariable Long id) {
        ProblemResponseDto problem = problemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/api/stats")
    public ResponseEntity<ProblemStatsDto> getStats() {
        ProblemStatsDto stats = problemService.getPlatformStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/api/seed")
    public ResponseEntity<Map<String, String>> seedData() {
        problemService.seedInitialData();
        Map<String, String> res = new HashMap<>();
        res.put("message", "Database seeded successfully with default problems.");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "BuildTheFix Java Spring Boot Backend");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
}
