package com.buildthefix.app.dto;

import com.buildthefix.app.entity.ProblemStatement;
import com.buildthefix.app.entity.ProblemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Response DTO representing problem statements and their reframed blueprints.
 * Includes @JsonProperty annotations to ensure 100% JSON compatibility with web clients.
 */
public class ProblemResponseDto {

    private Long id;

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("client_email")
    private String clientEmail;

    @JsonProperty("raw_title")
    private String rawTitle;

    @JsonProperty("raw_description")
    private String rawDescription;

    private ProblemStatus status;

    // AI Reframed Blueprint
    @JsonProperty("formal_title")
    private String formalTitle;

    @JsonProperty("target_persona")
    private String targetPersona;

    @JsonProperty("tech_stack")
    private List<String> techStack;

    @JsonProperty("core_features")
    private List<String> coreFeatures;

    private List<String> roadmap;

    // Duplicate Check Result Nested Object compatibility
    @JsonProperty("duplicate_check")
    private Map<String, Object> duplicateCheck = new HashMap<>();

    @JsonProperty("is_duplicate")
    private boolean isDuplicate;

    @JsonProperty("similar_problem_id")
    private Long similarProblemId;

    @JsonProperty("similarity_reason")
    private String similarityReason;

    // Developer details
    @JsonProperty("developer_name")
    private String developerName;

    @JsonProperty("developer_github")
    private String developerGithub;

    @JsonProperty("solution_url")
    private String solutionUrl;

    @JsonProperty("is_unlocked")
    private boolean isUnlocked;

    private List<SolutionResponseDto> solutions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProblemResponseDto() {}

    public ProblemResponseDto(ProblemStatement problem) {
        this.id = problem.getId();
        this.clientName = problem.getClientName();
        this.clientEmail = problem.getClientEmail();
        this.rawTitle = problem.getRawTitle();
        this.rawDescription = problem.getRawDescription();
        this.status = problem.getStatus();

        this.formalTitle = problem.getFormalTitle();
        this.targetPersona = problem.getTargetPersona();
        this.techStack = problem.getTechStack();
        this.coreFeatures = problem.getCoreFeatures();
        this.roadmap = problem.getRoadmap();

        this.isDuplicate = problem.isDuplicate();
        this.similarProblemId = problem.getSimilarProblemId();
        this.similarityReason = problem.getSimilarityReason();

        duplicateCheck.put("is_duplicate", this.isDuplicate);
        duplicateCheck.put("similar_problem_id", this.similarProblemId);
        duplicateCheck.put("similarity_reason", this.similarityReason != null ? this.similarityReason : "");

        if (problem.getClaimedDeveloper() != null) {
            this.developerName = problem.getClaimedDeveloper().getName();
            this.developerGithub = problem.getClaimedDeveloper().getGithubUsername();
        }

        if (problem.getSolutions() != null && !problem.getSolutions().isEmpty()) {
            this.solutions = problem.getSolutions().stream()
                    .map(SolutionResponseDto::new)
                    .collect(Collectors.toList());

            SolutionResponseDto topSol = this.solutions.get(0);
            if (this.developerName == null) {
                this.developerName = topSol.getDeveloperName();
                this.developerGithub = topSol.getDeveloperGithub();
            }
            this.solutionUrl = topSol.getDemoUrl();
            this.isUnlocked = topSol.isUnlocked();
        }

        this.createdAt = problem.getCreatedAt();
        this.updatedAt = problem.getUpdatedAt();
    }

    // Standard Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getRawTitle() {
        return rawTitle;
    }

    public void setRawTitle(String rawTitle) {
        this.rawTitle = rawTitle;
    }

    public String getRawDescription() {
        return rawDescription;
    }

    public void setRawDescription(String rawDescription) {
        this.rawDescription = rawDescription;
    }

    public ProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }

    public String getFormalTitle() {
        return formalTitle;
    }

    public void setFormalTitle(String formalTitle) {
        this.formalTitle = formalTitle;
    }

    public String getTargetPersona() {
        return targetPersona;
    }

    public void setTargetPersona(String targetPersona) {
        this.targetPersona = targetPersona;
    }

    public List<String> getTechStack() {
        return techStack;
    }

    public void setTechStack(List<String> techStack) {
        this.techStack = techStack;
    }

    public List<String> getCoreFeatures() {
        return coreFeatures;
    }

    public void setCoreFeatures(List<String> coreFeatures) {
        this.coreFeatures = coreFeatures;
    }

    public List<String> getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(List<String> roadmap) {
        this.roadmap = roadmap;
    }

    public Map<String, Object> getDuplicateCheck() {
        return duplicateCheck;
    }

    public void setDuplicateCheck(Map<String, Object> duplicateCheck) {
        this.duplicateCheck = duplicateCheck;
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

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDeveloperGithub() {
        return developerGithub;
    }

    public void setDeveloperGithub(String developerGithub) {
        this.developerGithub = developerGithub;
    }

    public String getSolutionUrl() {
        return solutionUrl;
    }

    public void setSolutionUrl(String solutionUrl) {
        this.solutionUrl = solutionUrl;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public List<SolutionResponseDto> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<SolutionResponseDto> solutions) {
        this.solutions = solutions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
