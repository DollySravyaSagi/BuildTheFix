package com.buildthefix.app.service;

import com.buildthefix.app.dto.DuplicateCheckDto;
import com.buildthefix.app.entity.ProblemStatement;
import com.buildthefix.app.service.impl.CosineSimilarityDuplicateDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DuplicateDetectorTest {

    private CosineSimilarityDuplicateDetector duplicateDetector;

    @BeforeEach
    void setUp() {
        duplicateDetector = new CosineSimilarityDuplicateDetector();
    }

    @Test
    @DisplayName("Should detect duplicate when domain topics overlap")
    void testDetectDuplicateTopic() {
        ProblemStatement existing = new ProblemStatement(
                "Sarah",
                "sarah@bakery.com",
                "WhatsApp Bakery Orders",
                "I lose cake sticky notes causing custom cake order mistakes."
        );
        existing.setId(101L);

        DuplicateCheckDto result = duplicateDetector.checkForDuplicates(
                "Custom Bakery Cake Tracker",
                "We need a system for bakery order calendar notes.",
                List.of(existing)
        );

        assertTrue(result.isDuplicate());
        assertEquals(101L, result.getSimilarProblemId());
        assertTrue(result.getSimilarityReason().contains("existing blueprint"));
    }

    @Test
    @DisplayName("Should return false when problem is completely unique")
    void testUniqueProblem() {
        ProblemStatement existing = new ProblemStatement(
                "Sarah",
                "sarah@bakery.com",
                "WhatsApp Bakery Orders",
                "I lose cake sticky notes causing custom cake order mistakes."
        );
        existing.setId(101L);

        DuplicateCheckDto result = duplicateDetector.checkForDuplicates(
                "Satellite Antenna Positioning System",
                "Calibrating orbital Dish alignment frequency vectors for space hardware.",
                List.of(existing)
        );

        assertFalse(result.isDuplicate());
        assertNull(result.getSimilarProblemId());
    }
}
