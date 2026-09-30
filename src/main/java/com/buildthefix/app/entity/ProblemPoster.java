package com.buildthefix.app.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * ProblemPoster subclass representing users who post real-world business bottlenecks.
 * Inherits common authentication and profile fields from User base class.
 */
@Entity
@DiscriminatorValue("PROBLEM_POSTER")
public class ProblemPoster extends User {

    private String companyName;

    public ProblemPoster() {
        super();
        setRole(UserRole.PROBLEM_POSTER);
    }

    public ProblemPoster(String name, String email, String password, String companyName) {
        super(name, email, password, UserRole.PROBLEM_POSTER);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
