package com.buildthefix.app.service.impl;

import com.buildthefix.app.dto.DuplicateCheckDto;
import com.buildthefix.app.entity.ProblemStatement;
import com.buildthefix.app.service.DuplicateDetector;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Advanced DuplicateDetector implementation using Token Similarity and Topic Overlap Analysis.
 * Annotated with @Primary to be selected by Spring Dependency Injection.
 */
@Service
@Primary
public class CosineSimilarityDuplicateDetector implements DuplicateDetector {

    private static final double SIMILARITY_THRESHOLD = 0.30;

    @Override
    public DuplicateCheckDto checkForDuplicates(String rawTitle, String rawDescription, List<ProblemStatement> existingProblems) {
        if (existingProblems == null || existingProblems.isEmpty()) {
            return new DuplicateCheckDto(false, null, "");
        }

        String inputCombined = (rawTitle + " " + rawDescription).toLowerCase();
        Set<String> inputTokens = tokenize(inputCombined);

        double highestSimilarity = 0.0;
        ProblemStatement mostSimilarProblem = null;
        String topicMatchLabel = null;

        // Topic-based categorization dictionaries
        Map<String, String> commonTopics = Map.of(
                "bakery", "Bakery or custom cake orders",
                "cake", "Bakery or custom cake orders",
                "timesheet", "Contractor billable hours & timesheets",
                "contractor", "Contractor billable hours & timesheets",
                "invoice", "Invoice processing & expense tracking",
                "receipt", "Invoice processing & expense tracking",
                "appointment", "Appointment booking & client reminders",
                "clinic", "Appointment booking & client reminders",
                "inventory", "Inventory stock management",
                "craft", "Handmade crafts & boutique shop orders"
        );

        for (ProblemStatement existing : existingProblems) {
            String existingCombined = (existing.getRawTitle() + " " + existing.getRawDescription()).toLowerCase();
            Set<String> existingTokens = tokenize(existingCombined);

            // 1. Calculate Jaccard / Token Cosine Similarity
            double similarity = calculateJaccardSimilarity(inputTokens, existingTokens);

            // 2. Check for Topic Keyword Matches
            String topicLabel = null;
            for (Map.Entry<String, String> entry : commonTopics.entrySet()) {
                if (inputCombined.contains(entry.getKey()) && existingCombined.contains(entry.getKey())) {
                    topicLabel = entry.getValue();
                    similarity = Math.max(similarity, 0.65); // Boost similarity score on direct domain topic overlap
                    break;
                }
            }

            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                mostSimilarProblem = existing;
                topicMatchLabel = topicLabel;
            }
        }

        if (highestSimilarity >= SIMILARITY_THRESHOLD && mostSimilarProblem != null) {
            String reason;
            if (topicMatchLabel != null) {
                reason = String.format("High concept overlap detected with existing blueprint: \"%s\" (ID: %d, Topic: %s).",
                        mostSimilarProblem.getRawTitle(), mostSimilarProblem.getId(), topicMatchLabel);
            } else {
                reason = String.format("High text similarity (%.0f%% match) with existing problem: \"%s\" (ID: %d).",
                        highestSimilarity * 100, mostSimilarProblem.getRawTitle(), mostSimilarProblem.getId());
            }

            return new DuplicateCheckDto(true, mostSimilarProblem.getId(), reason);
        }

        return new DuplicateCheckDto(false, null, "");
    }

    private Set<String> tokenize(String text) {
        String[] words = text.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
        Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "in", "on", "at", "to", "for", "with", "is", "was", "my", "i", "we", "our");
        Set<String> tokens = new HashSet<>();
        for (String word : words) {
            if (word.length() > 2 && !stopWords.contains(word)) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() || set2.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }
}
