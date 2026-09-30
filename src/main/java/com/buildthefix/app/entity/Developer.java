package com.buildthefix.app.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Developer subclass representing technical builders who claim problems and submit solution links.
 * Inherits common authentication details from User base class.
 */
@Entity
@DiscriminatorValue("DEVELOPER")
public class Developer extends User {

    private String githubUsername;
    private String portfolioUrl;

    public Developer() {
        super();
        setRole(UserRole.DEVELOPER);
    }

    public Developer(String name, String email, String password, String githubUsername, String portfolioUrl) {
        super(name, email, password, UserRole.DEVELOPER);
        this.githubUsername = githubUsername;
        this.portfolioUrl = portfolioUrl;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
}
