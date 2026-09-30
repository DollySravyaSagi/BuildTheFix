package com.buildthefix.app.repository;

import com.buildthefix.app.entity.ProblemPoster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemPosterRepository extends JpaRepository<ProblemPoster, Long> {
}
