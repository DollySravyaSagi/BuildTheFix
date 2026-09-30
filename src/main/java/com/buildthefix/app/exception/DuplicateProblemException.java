package com.buildthefix.app.exception;

public class DuplicateProblemException extends RuntimeException {
    private final Long similarProblemId;

    public DuplicateProblemException(String message, Long similarProblemId) {
        super(message);
        this.similarProblemId = similarProblemId;
    }

    public Long getSimilarProblemId() {
        return similarProblemId;
    }
}
