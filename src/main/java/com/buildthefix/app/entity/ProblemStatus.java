package com.buildthefix.app.entity;

/**
 * Lifecycle status of a problem statement on the platform.
 * OPEN: Posted, waiting for a developer to claim or submit a solution.
 * IN_PROGRESS: Developer has claimed the problem and is building a demo.
 * SOLVED: Developer submitted a working solution demo.
 */
public enum ProblemStatus {
    OPEN,
    IN_PROGRESS,
    SOLVED
}
