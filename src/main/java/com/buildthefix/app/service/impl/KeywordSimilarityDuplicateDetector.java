package com.buildthefix.app.service.impl;

import com.buildthefix.app.dto.DuplicateCheckDto;
import com.buildthefix.app.entity.ProblemStatement;
import com.buildthefix.app.service.DuplicateDetector;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of DuplicateDetector analyzing keyword & topic overlap against existing problem statements.
 */
@Service
public class KeywordSimilarityDuplicateDetector implements DuplicateDetector {

    @Override
    public DuplicateCheckDto checkForDuplicates(String rawTitle, String rawDescription, List<ProblemStatement> existingProblems) {
        if (existingProblems == null || existingProblems.isEmpty()) {
            return new DuplicateCheckDto(false, null, "");
        }

        String combined = (rawTitle + " " + rawDescription).toLowerCase();

        String[] topics = {"bakery", "cake", "invoice", "receipt", "timesheet", "contractor", "appointment", "clinic", "inventory"};

        for (ProblemStatement existing : existingProblems) {
            String existingText = (existing.getRawTitle() + " " + existing.getRawDescription()).toLowerCase();

            for (String topic : topics) {
                if (combined.contains(topic) && existingText.contains(topic)) {
                    return new DuplicateCheckDto(
                            true,
                            existing.getId(),
                            "High concept overlap detected with existing blueprint: \"" + existing.getRawTitle() + "\" (Topic: " + topic + ")."
                    );
                }
            }
        }

        return new DuplicateCheckDto(false, null, "");
    }
}
