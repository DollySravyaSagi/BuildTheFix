package com.buildthefix.app.service;

import com.buildthefix.app.dto.DuplicateCheckDto;
import com.buildthefix.app.entity.ProblemStatement;
import java.util.List;

/**
 * Interface defining the contract for checking duplicate problem submissions.
 */
public interface DuplicateDetector {
    DuplicateCheckDto checkForDuplicates(String rawTitle, String rawDescription, List<ProblemStatement> existingProblems);
}
