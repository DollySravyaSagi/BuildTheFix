package com.buildthefix.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Data Transfer Object containing marketplace statistics.
 * Annotated with @JsonProperty for frontend compatibility.
 */
public class ProblemStatsDto {

    @JsonProperty("total_problems")
    private long totalProblems;

    @JsonProperty("solved_problems")
    private long solvedProblems;

    @JsonProperty("in_progress")
    private long inProgress;

    @JsonProperty("open_problems")
    private long openProblems;

    @JsonProperty("unlocked_count")
    private long unlockedCount;

    @JsonProperty("total_mrr")
    private BigDecimal totalMrr;

    public ProblemStatsDto() {}

    public ProblemStatsDto(long totalProblems, long solvedProblems, long inProgress, long openProblems, long unlockedCount, BigDecimal totalMrr) {
        this.totalProblems = totalProblems;
        this.solvedProblems = solvedProblems;
        this.inProgress = inProgress;
        this.openProblems = openProblems;
        this.unlockedCount = unlockedCount;
        this.totalMrr = totalMrr;
    }

    public long getTotalProblems() {
        return totalProblems;
    }

    public void setTotalProblems(long totalProblems) {
        this.totalProblems = totalProblems;
    }

    public long getSolvedProblems() {
        return solvedProblems;
    }

    public void setSolvedProblems(long solvedProblems) {
        this.solvedProblems = solvedProblems;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getOpenProblems() {
        return openProblems;
    }

    public void setOpenProblems(long openProblems) {
        this.openProblems = openProblems;
    }

    public long getUnlockedCount() {
        return unlockedCount;
    }

    public void setUnlockedCount(long unlockedCount) {
        this.unlockedCount = unlockedCount;
    }

    public BigDecimal getTotalMrr() {
        return totalMrr;
    }

    public void setTotalMrr(BigDecimal totalMrr) {
        this.totalMrr = totalMrr;
    }
}
