package com.buildthefix.app.service;

import com.buildthefix.app.dto.ProblemBlueprintDto;

/**
 * Interface defining the contract for AI-powered problem reframing services.
 * Follows Interface Segregation Principle allowing Gemini or mock implementations.
 */
public interface AiReframingService {
    ProblemBlueprintDto reframeProblem(String rawTitle, String rawDescription);
}
