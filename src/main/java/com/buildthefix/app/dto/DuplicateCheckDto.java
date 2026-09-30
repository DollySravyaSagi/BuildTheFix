package com.buildthefix.app.dto;

/**
 * DTO holding duplicate detection outcome.
 */
public class DuplicateCheckDto {
    private boolean isDuplicate;
    private Long similarProblemId;
    private String similarityReason;

    public DuplicateCheckDto() {}

    public DuplicateCheckDto(boolean isDuplicate, Long similarProblemId, String similarityReason) {
        this.isDuplicate = isDuplicate;
        this.similarProblemId = similarProblemId;
        this.similarityReason = similarityReason;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public Long getSimilarProblemId() {
        return similarProblemId;
    }

    public void setSimilarProblemId(Long similarProblemId) {
        this.similarProblemId = similarProblemId;
    }

    public String getSimilarityReason() {
        return similarityReason;
    }

    public void setSimilarityReason(String similarityReason) {
        this.similarityReason = similarityReason;
    }
}
