package com.buildthefix.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user-submitted problem statement and its reframed SaaS blueprint.
 */
@Entity
@Table(name = "problem_statements")
public class ProblemStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String clientEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id")
    private ProblemPoster poster;

    @Column(nullable = false, length = 500)
    private String rawTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rawDescription;

    // Gemini Reframed Blueprint Fields
    @Column(length = 500)
    private String formalTitle;

    @Column(length = 500)
    private String targetPersona;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_tech_stack", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "tech_item")
    private List<String> techStack = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_core_features", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "feature", columnDefinition = "TEXT")
    private List<String> coreFeatures = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_roadmap", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "step", columnDefinition = "TEXT")
    private List<String> roadmap = new ArrayList<>();

    // Duplicate Detection Fields
    private boolean isDuplicate = false;
    private Long similarProblemId;

    @Column(columnDefinition = "TEXT")
    private String similarityReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemStatus status = ProblemStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_developer_id")
    private Developer claimedDeveloper;

    @OneToMany(mappedBy = "problemStatement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solution> solutions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProblemStatement() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ProblemStatement(String clientName, String clientEmail, String rawTitle, String rawDescription) {
        this();
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.rawTitle = rawTitle;
        this.rawDescription = rawDescription;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Encapsulated Getters & Setters
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

    public ProblemPoster getPoster() {
        return poster;
    }

    public void setPoster(ProblemPoster poster) {
        this.poster = poster;
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

    public ProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }

    public Developer getClaimedDeveloper() {
        return claimedDeveloper;
    }

    public void setClaimedDeveloper(Developer claimedDeveloper) {
        this.claimedDeveloper = claimedDeveloper;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
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
