package com.buildthefix.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a software solution submitted by a developer for a ProblemStatement.
 * Contains demo links (publicly viewable) and locked full access repositories (requires payment).
 */
@Entity
@Table(name = "solutions")
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_statement_id", nullable = false)
    private ProblemStatement problemStatement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id")
    private Developer developer;

    private String developerName;
    private String developerGithub;

    @Column(nullable = false)
    private String demoUrl;

    @Column(length = 500)
    private String fullAccessCodeUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = new BigDecimal("49.00");

    @Column(nullable = false)
    private boolean isUnlocked = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "solution_unlocked_emails", joinColumns = @JoinColumn(name = "solution_id"))
    @Column(name = "user_email")
    private List<String> unlockedByEmails = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Solution() {
        this.createdAt = LocalDateTime.now();
    }

    public Solution(ProblemStatement problemStatement, String developerName, String developerGithub, String demoUrl, BigDecimal price) {
        this();
        this.problemStatement = problemStatement;
        this.developerName = developerName;
        this.developerGithub = developerGithub;
        this.demoUrl = demoUrl;
        if (price != null) {
            this.price = price;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProblemStatement getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(ProblemStatement problemStatement) {
        this.problemStatement = problemStatement;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
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

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public String getFullAccessCodeUrl() {
        return fullAccessCodeUrl;
    }

    public void setFullAccessCodeUrl(String fullAccessCodeUrl) {
        this.fullAccessCodeUrl = fullAccessCodeUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public List<String> getUnlockedByEmails() {
        return unlockedByEmails;
    }

    public void setUnlockedByEmails(List<String> unlockedByEmails) {
        this.unlockedByEmails = unlockedByEmails;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
